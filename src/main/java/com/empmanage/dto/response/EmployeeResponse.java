package com.empmanage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {
    private Long id;
    private String name;
    private String mobile;
    private BigDecimal baseSalary;
    private BigDecimal pfAmount;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


