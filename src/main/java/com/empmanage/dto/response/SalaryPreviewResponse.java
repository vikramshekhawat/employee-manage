package com.empmanage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryPreviewResponse {
    private Long employeeId;
    private String employeeName;
    private String employeeMobile;
    private Integer month;
    private Integer year;
    private BigDecimal baseSalary;
    private BigDecimal totalOvertime;
    private BigDecimal totalAdvances;
    private BigDecimal unpaidLeaveDays;
    private BigDecimal leaveDeduction;
    private BigDecimal pfDeduction;
    private BigDecimal finalSalary;
    private List<SalaryDetailItem> dateWiseBreakdown;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalaryDetailItem {
        private String type; // OVERTIME, ADVANCE, LEAVE
        private String date;
        private BigDecimal amount;
        private String description;
    }
}


