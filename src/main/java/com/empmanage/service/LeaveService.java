package com.empmanage.service;

import com.empmanage.dto.request.LeaveRequest;
import com.empmanage.entity.Leave;
import com.empmanage.entity.Employee;
import com.empmanage.repository.LeaveRepository;
import com.empmanage.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRepository leaveRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public Leave createLeave(LeaveRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + request.getEmployeeId()));

        Leave leave = new Leave();
        leave.setEmployee(employee);
        leave.setLeaveDate(request.getLeaveDate());
        leave.setLeaveType(request.getLeaveType());
        leave.setDescription(request.getDescription());

        return leaveRepository.save(leave);
    }

    public List<Leave> getLeavesByEmployeeId(Long employeeId) {
        return leaveRepository.findByEmployeeId(employeeId);
    }

    public List<Leave> getLeavesByEmployeeIdAndMonth(Long employeeId, Integer month, Integer year) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        return leaveRepository.findByEmployeeIdAndDateRange(employeeId, startDate, endDate);
    }

    public List<Leave> getUnpaidLeavesByEmployeeIdAndMonth(Long employeeId, Integer month, Integer year) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        return leaveRepository.findUnpaidLeavesByEmployeeIdAndDateRange(employeeId, startDate, endDate);
    }
}


