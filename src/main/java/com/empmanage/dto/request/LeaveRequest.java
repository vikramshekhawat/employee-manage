package com.empmanage.dto.request;

import com.empmanage.entity.Leave;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveRequest {
    @NotNull(message = "Employee ID is required")
    private Long employeeId;
    
    @NotNull(message = "Leave date is required")
    private LocalDate leaveDate;
    
    @NotNull(message = "Leave type is required")
    private Leave.LeaveType leaveType;
    
    private String description;
}


