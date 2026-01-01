package com.empmanage.controller;

import com.empmanage.dto.request.SalaryGenerationRequest;
import com.empmanage.dto.response.ApiResponse;
import com.empmanage.dto.response.SalaryPreviewResponse;
import com.empmanage.entity.Salary;
import com.empmanage.service.SalaryCalculationService;
import com.empmanage.service.SmsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/api/salary")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class SalaryController {

    private final SalaryCalculationService salaryCalculationService;
    private final SmsService smsService;

    @PostMapping("/preview")
    public ResponseEntity<ApiResponse<SalaryPreviewResponse>> previewSalary(
            @Valid @RequestBody SalaryGenerationRequest request) {
        SalaryPreviewResponse preview = salaryCalculationService.previewSalary(
                request.getEmployeeId(), request.getMonth(), request.getYear());
        return ResponseEntity.ok(ApiResponse.success(preview));
    }

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<Salary>> generateSalary(@Valid @RequestBody SalaryGenerationRequest request) {
        Salary salary = salaryCalculationService.generateSalary(
                request.getEmployeeId(), request.getMonth(), request.getYear());
        
        // Send SMS after generating salary
        try {
            smsService.sendSalarySms(salary.getId());
        } catch (Exception e) {
            // Log error but don't fail the request
            System.err.println("Failed to send SMS: " + e.getMessage());
        }
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Salary generated and SMS sent successfully", salary));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<List<Salary>>> getSalaryHistory(@PathVariable Long employeeId) {
        List<Salary> salaries = salaryCalculationService.getSalaryHistory(employeeId);
        return ResponseEntity.ok(ApiResponse.success(salaries));
    }

    @PostMapping("/{salaryId}/resend-sms")
    public ResponseEntity<ApiResponse<Object>> resendSms(@PathVariable Long salaryId) {
        smsService.sendSalarySms(salaryId);
        return ResponseEntity.ok(ApiResponse.success("SMS resent successfully", null));
    }
}


