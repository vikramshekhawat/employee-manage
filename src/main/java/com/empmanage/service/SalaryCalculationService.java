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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + employeeId));

        BigDecimal baseSalary = employee.getBaseSalary();
        
        // Get all data for the month
        List<Overtime> overtimes = overtimeRepository.findByEmployeeIdAndMonthAndYear(employeeId, month, year);
        List<Advance> advances = advanceRepository.findByEmployeeIdAndMonthAndYear(employeeId, month, year);
        List<Leave> unpaidLeaves = leaveRepository.findUnpaidLeavesByEmployeeIdAndMonthAndYear(employeeId, month, year);

        // Calculate totals
        BigDecimal totalOvertime = overtimes.stream()
                .map(Overtime::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAdvances = advances.stream()
                .map(Advance::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal unpaidLeaveDays = BigDecimal.valueOf(unpaidLeaves.size());
        BigDecimal dailySalary = baseSalary.divide(BigDecimal.valueOf(30), 2, RoundingMode.HALF_UP);
        BigDecimal leaveDeduction = unpaidLeaveDays.multiply(dailySalary);

        BigDecimal pfDeduction = baseSalary.multiply(employee.getPfPercentage())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // Final Salary = Base Salary + Overtime - Advances - PF - Leave Deduction
        BigDecimal finalSalary = baseSalary
                .add(totalOvertime)
                .subtract(totalAdvances)
                .subtract(pfDeduction)
                .subtract(leaveDeduction);

        // Create date-wise breakdown
        List<SalaryPreviewResponse.SalaryDetailItem> breakdown = new ArrayList<>();
        
        // Add overtime entries
        overtimes.forEach(ot -> {
            breakdown.add(new SalaryPreviewResponse.SalaryDetailItem(
                    "OVERTIME",
                    ot.getOvertimeDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                    ot.getTotalAmount(),
                    ot.getHours() + " hrs @ " + ot.getRatePerHour() + "/hr"
            ));
        });

        // Add advance entries
        advances.forEach(adv -> {
            breakdown.add(new SalaryPreviewResponse.SalaryDetailItem(
                    "ADVANCE",
                    adv.getAdvanceDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                    adv.getAmount().negate(), // Negative for deduction
                    adv.getDescription() != null ? adv.getDescription() : "Advance"
            ));
        });

        // Add unpaid leave entries
        unpaidLeaves.forEach(leave -> {
            breakdown.add(new SalaryPreviewResponse.SalaryDetailItem(
                    "LEAVE",
                    leave.getLeaveDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                    dailySalary.negate(), // Negative for deduction
                    "Unpaid Leave" + (leave.getDescription() != null ? ": " + leave.getDescription() : "")
            ));
        });

        // Sort by date
        breakdown.sort((a, b) -> LocalDate.parse(a.getDate()).compareTo(LocalDate.parse(b.getDate())));

        SalaryPreviewResponse response = new SalaryPreviewResponse();
        response.setEmployeeId(employee.getId());
        response.setEmployeeName(employee.getName());
        response.setEmployeeMobile(employee.getMobile());
        response.setMonth(month);
        response.setYear(year);
        response.setBaseSalary(baseSalary);
        response.setTotalOvertime(totalOvertime);
        response.setTotalAdvances(totalAdvances);
        response.setUnpaidLeaveDays(unpaidLeaveDays);
        response.setLeaveDeduction(leaveDeduction);
        response.setPfDeduction(pfDeduction);
        response.setFinalSalary(finalSalary);
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

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + employeeId));

        BigDecimal baseSalary = employee.getBaseSalary();
        
        // Get all data for the month
        List<Overtime> overtimes = overtimeRepository.findByEmployeeIdAndMonthAndYear(employeeId, month, year);
        List<Advance> advances = advanceRepository.findByEmployeeIdAndMonthAndYear(employeeId, month, year);
        List<Leave> unpaidLeaves = leaveRepository.findUnpaidLeavesByEmployeeIdAndMonthAndYear(employeeId, month, year);

        // Calculate totals
        BigDecimal totalOvertime = overtimes.stream()
                .map(Overtime::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAdvances = advances.stream()
                .map(Advance::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal unpaidLeaveDays = BigDecimal.valueOf(unpaidLeaves.size());
        BigDecimal dailySalary = baseSalary.divide(BigDecimal.valueOf(30), 2, RoundingMode.HALF_UP);
        BigDecimal leaveDeduction = unpaidLeaveDays.multiply(dailySalary);

        BigDecimal pfDeduction = baseSalary.multiply(employee.getPfPercentage())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // Final Salary = Base Salary + Overtime - Advances - PF - Leave Deduction
        BigDecimal finalSalary = baseSalary
                .add(totalOvertime)
                .subtract(totalAdvances)
                .subtract(pfDeduction)
                .subtract(leaveDeduction);

        // Create and save Salary entity
        Salary salary = new Salary();
        salary.setEmployee(employee);
        salary.setMonth(month);
        salary.setYear(year);
        salary.setBaseSalary(baseSalary);
        salary.setTotalOvertime(totalOvertime);
        salary.setTotalAdvances(totalAdvances);
        salary.setTotalLeaves(leaveDeduction);
        salary.setPfDeduction(pfDeduction);
        salary.setFinalSalary(finalSalary);
        salary.setSmsSent(false);

        Salary savedSalary = salaryRepository.save(salary);

        // Create salary details
        List<SalaryDetail> details = new ArrayList<>();

        // Add overtime details
        overtimes.forEach(ot -> {
            SalaryDetail detail = new SalaryDetail();
            detail.setSalary(savedSalary);
            detail.setType(SalaryDetail.DetailType.OVERTIME);
            detail.setDate(ot.getOvertimeDate());
            detail.setAmount(ot.getTotalAmount());
            detail.setDescription(ot.getHours() + " hrs @ " + ot.getRatePerHour() + "/hr");
            details.add(detail);
        });

        // Add advance details
        advances.forEach(adv -> {
            SalaryDetail detail = new SalaryDetail();
            detail.setSalary(savedSalary);
            detail.setType(SalaryDetail.DetailType.ADVANCE);
            detail.setDate(adv.getAdvanceDate());
            detail.setAmount(adv.getAmount().negate());
            detail.setDescription(adv.getDescription() != null ? adv.getDescription() : "Advance");
            details.add(detail);
        });

        // Add unpaid leave details
        unpaidLeaves.forEach(leave -> {
            SalaryDetail detail = new SalaryDetail();
            detail.setSalary(savedSalary);
            detail.setType(SalaryDetail.DetailType.LEAVE);
            detail.setDate(leave.getLeaveDate());
            detail.setAmount(dailySalary.negate());
            detail.setDescription("Unpaid Leave" + (leave.getDescription() != null ? ": " + leave.getDescription() : ""));
            details.add(detail);
        });

        salaryDetailRepository.saveAll(details);

        return savedSalary;
    }

    public List<Salary> getSalaryHistory(Long employeeId) {
        return salaryRepository.findByEmployeeIdOrderByYearDescMonthDesc(employeeId);
    }
}


