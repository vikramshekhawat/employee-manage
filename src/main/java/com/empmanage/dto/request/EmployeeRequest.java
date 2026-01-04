package com.empmanage.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EmployeeRequest {
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
    private String mobile;
    
    @NotNull(message = "Base salary is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base salary must be greater than 0")
    private BigDecimal baseSalary;
    
    @NotNull(message = "PF amount is required")
    @DecimalMin(value = "0.0", message = "PF amount must be 0 or greater")
    private BigDecimal pfAmount;
}


