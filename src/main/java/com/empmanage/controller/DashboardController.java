package com.empmanage.controller;

import com.empmanage.dto.response.ApiResponse;
import com.empmanage.dto.response.DashboardResponse;
import com.empmanage.repository.EmployeeRepository;
import com.empmanage.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final EmployeeRepository employeeRepository;
    private final SalaryRepository salaryRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard() {
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();
        int lastMonth = currentMonth == 1 ? 12 : currentMonth - 1;
        int lastMonthYear = currentMonth == 1 ? currentYear - 1 : currentYear;

        long totalEmployees = employeeRepository.count();
        long activeEmployees = employeeRepository.findByActiveTrue().size();
        
        BigDecimal totalSalaryThisMonth = salaryRepository.getTotalSalaryForMonth(currentMonth, currentYear);
        if (totalSalaryThisMonth == null) {
            totalSalaryThisMonth = BigDecimal.ZERO;
        }
        
        BigDecimal totalSalaryLastMonth = salaryRepository.getTotalSalaryForMonth(lastMonth, lastMonthYear);
        if (totalSalaryLastMonth == null) {
            totalSalaryLastMonth = BigDecimal.ZERO;
        }

        // Count employees without salary for current month
        long pendingSalaryGenerations = employeeRepository.findByActiveTrue().stream()
                .filter(emp -> salaryRepository.findByEmployeeIdAndMonthAndYear(
                        emp.getId(), currentMonth, currentYear).isEmpty())
                .count();

        DashboardResponse response = new DashboardResponse();
        response.setTotalEmployees(totalEmployees);
        response.setActiveEmployees(activeEmployees);
        response.setTotalSalaryThisMonth(totalSalaryThisMonth);
        response.setTotalSalaryLastMonth(totalSalaryLastMonth);
        response.setPendingSalaryGenerations(pendingSalaryGenerations);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}


