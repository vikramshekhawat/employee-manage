package com.empmanage.service;

import com.empmanage.dto.response.SalaryPreviewResponse;
import com.empmanage.entity.*;
import com.empmanage.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalaryCalculationService {

    private final EmployeeRepository employeeRepository;
    private final AdvanceRepository advanceRepository;
    private final LeaveRepository leaveRepository;
    private final OvertimeRepository overtimeRepository;
    private final SalaryRepository salaryRepository;
    private final SalaryDetailRepository salaryDetailRepository;

    public SalaryPreviewResponse previewSalary(Long employeeId, Integer month, Integer year) {
        SalaryCalculationData data = calculateSalaryData(employeeId, month, year);

        // Create date-wise breakdown
        List<SalaryPreviewResponse.SalaryDetailItem> breakdown = new ArrayList<>();
        
        // Add overtime entries
        data.overtimes.forEach(ot -> {
            breakdown.add(new SalaryPreviewResponse.SalaryDetailItem(
                    "OVERTIME",
                    ot.getOvertimeDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                    ot.getTotalAmount(),
                    ot.getHours() + " hrs @ " + ot.getRatePerHour() + "/hr"
            ));
        });

        // Add advance entries
        data.advances.forEach(adv -> {
            breakdown.add(new SalaryPreviewResponse.SalaryDetailItem(
                    "ADVANCE",
                    adv.getAdvanceDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                    adv.getAmount().negate(), // Negative for deduction
                    adv.getDescription() != null ? adv.getDescription() : "Advance"
            ));
        });

        // Add unpaid leave entries
        data.unpaidLeaves.forEach(leave -> {
            breakdown.add(new SalaryPreviewResponse.SalaryDetailItem(
                    "LEAVE",
                    leave.getLeaveDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                    data.dailySalary.negate(), // Negative for deduction
                    "Unpaid Leave" + (leave.getDescription() != null ? ": " + leave.getDescription() : "")
            ));
        });

        // Sort by date
        breakdown.sort((a, b) -> LocalDate.parse(a.getDate()).compareTo(LocalDate.parse(b.getDate())));

        SalaryPreviewResponse response = new SalaryPreviewResponse();
        response.setEmployeeId(data.employee.getId());
        response.setEmployeeName(data.employee.getName());
        response.setEmployeeMobile(data.employee.getMobile());
        response.setMonth(month);
        response.setYear(year);
        response.setBaseSalary(data.baseSalary);
        response.setTotalOvertime(data.totalOvertime);
        response.setTotalAdvances(data.totalAdvances);
        response.setUnpaidLeaveDays(data.unpaidLeaveDays);
        response.setLeaveDeduction(data.leaveDeduction);
        response.setPfDeduction(data.pfDeduction);
        response.setFinalSalary(data.finalSalary);
        response.setDateWiseBreakdown(breakdown);

        return response;
    }

    @Transactional
    public Salary generateSalary(Long employeeId, Integer month, Integer year) {
        // Check if salary already exists
        if (salaryRepository.findByEmployeeIdAndMonthAndYear(employeeId, month, year).isPresent()) {
            throw new IllegalArgumentException("Salary for employee " + employeeId + 
                    " for month " + month + "/" + year + " already exists");
        }

        SalaryCalculationData data = calculateSalaryData(employeeId, month, year);

        // Create and save Salary entity
        Salary salary = new Salary();
        salary.setEmployee(data.employee);
        salary.setMonth(month);
        salary.setYear(year);
        salary.setBaseSalary(data.baseSalary);
        salary.setTotalOvertime(data.totalOvertime);
        salary.setTotalAdvances(data.totalAdvances);
        salary.setTotalLeaves(data.leaveDeduction);
        salary.setPfDeduction(data.pfDeduction);
        salary.setFinalSalary(data.finalSalary);
        salary.setSmsSent(false);

        Salary savedSalary = salaryRepository.save(salary);

        // Create salary details
        List<SalaryDetail> details = new ArrayList<>();

        // Add overtime details
        data.overtimes.forEach(ot -> {
            SalaryDetail detail = new SalaryDetail();
            detail.setSalary(savedSalary);
            detail.setType(SalaryDetail.DetailType.OVERTIME);
            detail.setDate(ot.getOvertimeDate());
            detail.setAmount(ot.getTotalAmount());
            detail.setDescription(ot.getHours() + " hrs @ " + ot.getRatePerHour() + "/hr");
            details.add(detail);
        });

        // Add advance details
        data.advances.forEach(adv -> {
            SalaryDetail detail = new SalaryDetail();
            detail.setSalary(savedSalary);
            detail.setType(SalaryDetail.DetailType.ADVANCE);
            detail.setDate(adv.getAdvanceDate());
            detail.setAmount(adv.getAmount().negate());
            detail.setDescription(adv.getDescription() != null ? adv.getDescription() : "Advance");
            details.add(detail);
        });

        // Add unpaid leave details
        data.unpaidLeaves.forEach(leave -> {
            SalaryDetail detail = new SalaryDetail();
            detail.setSalary(savedSalary);
            detail.setType(SalaryDetail.DetailType.LEAVE);
            detail.setDate(leave.getLeaveDate());
            detail.setAmount(data.dailySalary.negate());
            detail.setDescription("Unpaid Leave" + (leave.getDescription() != null ? ": " + leave.getDescription() : ""));
            details.add(detail);
        });

        salaryDetailRepository.saveAll(details);

        return savedSalary;
    }

    private SalaryCalculationData calculateSalaryData(Long employeeId, Integer month, Integer year) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + employeeId));

        BigDecimal baseSalary = employee.getBaseSalary();
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // Get all data for the month
        List<Overtime> overtimes = overtimeRepository.findByEmployeeIdAndDateRange(employeeId, startDate, endDate);
        List<Advance> advances = advanceRepository.findByEmployeeIdAndDateRange(employeeId, startDate, endDate);
        List<Leave> unpaidLeaves = leaveRepository.findUnpaidLeavesByEmployeeIdAndDateRange(employeeId, startDate, endDate);

        // Calculate totals
        BigDecimal totalOvertime = overtimes.stream()
                .map(Overtime::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAdvances = advances.stream()
                .map(Advance::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal unpaidLeaveDays = BigDecimal.valueOf(unpaidLeaves.size());
        BigDecimal dailySalary = baseSalary.divide(BigDecimal.valueOf(yearMonth.lengthOfMonth()), 2, RoundingMode.HALF_UP);
        BigDecimal leaveDeduction = unpaidLeaveDays.multiply(dailySalary);

        BigDecimal pfDeduction = baseSalary.multiply(employee.getPfPercentage())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // Final Salary = Base Salary + Overtime - Advances - PF - Leave Deduction
        BigDecimal finalSalary = baseSalary
                .add(totalOvertime)
                .subtract(totalAdvances)
                .subtract(pfDeduction)
                .subtract(leaveDeduction);

        return new SalaryCalculationData(
            employee, baseSalary, overtimes, advances, unpaidLeaves,
            totalOvertime, totalAdvances, unpaidLeaveDays, dailySalary,
            leaveDeduction, pfDeduction, finalSalary
        );
    }

    public List<Salary> getSalaryHistory(Long employeeId) {
        return salaryRepository.findByEmployeeIdOrderByYearDescMonthDesc(employeeId);
    }

    private static class SalaryCalculationData {
        final Employee employee;
        final BigDecimal baseSalary;
        final List<Overtime> overtimes;
        final List<Advance> advances;
        final List<Leave> unpaidLeaves;
        final BigDecimal totalOvertime;
        final BigDecimal totalAdvances;
        final BigDecimal unpaidLeaveDays;
        final BigDecimal dailySalary;
        final BigDecimal leaveDeduction;
        final BigDecimal pfDeduction;
        final BigDecimal finalSalary;

        public SalaryCalculationData(Employee employee, BigDecimal baseSalary, List<Overtime> overtimes,
                                     List<Advance> advances, List<Leave> unpaidLeaves, BigDecimal totalOvertime,
                                     BigDecimal totalAdvances, BigDecimal unpaidLeaveDays, BigDecimal dailySalary,
                                     BigDecimal leaveDeduction, BigDecimal pfDeduction, BigDecimal finalSalary) {
            this.employee = employee;
            this.baseSalary = baseSalary;
            this.overtimes = overtimes;
            this.advances = advances;
            this.unpaidLeaves = unpaidLeaves;
            this.totalOvertime = totalOvertime;
            this.totalAdvances = totalAdvances;
            this.unpaidLeaveDays = unpaidLeaveDays;
            this.dailySalary = dailySalary;
            this.leaveDeduction = leaveDeduction;
            this.pfDeduction = pfDeduction;
            this.finalSalary = finalSalary;
        }
    }
}
