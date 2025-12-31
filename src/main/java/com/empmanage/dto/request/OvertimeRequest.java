package com.empmanage.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OvertimeRequest {
    @NotNull(message = "Employee ID is required")
    private Long employeeId;
    
    @NotNull(message = "Overtime date is required")
    private LocalDate overtimeDate;
    
    @NotNull(message = "Hours is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Hours must be greater than 0")
    private BigDecimal hours;
    
    @NotNull(message = "Rate per hour is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Rate per hour must be greater than 0")
    private BigDecimal ratePerHour;
}


