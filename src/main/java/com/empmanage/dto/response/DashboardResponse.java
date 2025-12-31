package com.empmanage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private Long totalEmployees;
    private Long activeEmployees;
    private BigDecimal totalSalaryThisMonth;
    private BigDecimal totalSalaryLastMonth;
    private Long pendingSalaryGenerations;
}


