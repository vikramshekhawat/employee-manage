package com.empmanage.controller;

import com.empmanage.dto.request.OvertimeRequest;
import com.empmanage.dto.response.ApiResponse;
import com.empmanage.entity.Overtime;
import com.empmanage.service.OvertimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/overtimes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OvertimeController {

    private final OvertimeService overtimeService;

    @PostMapping
    public ResponseEntity<ApiResponse<Overtime>> createOvertime(@Valid @RequestBody OvertimeRequest request) {
        Overtime overtime = overtimeService.createOvertime(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Overtime created successfully", overtime));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<List<Overtime>>> getOvertimesByEmployee(@PathVariable Long employeeId) {
        List<Overtime> overtimes = overtimeService.getOvertimesByEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.success(overtimes));
    }

    @GetMapping("/employee/{employeeId}/month/{month}/year/{year}")
    public ResponseEntity<ApiResponse<List<Overtime>>> getOvertimesByEmployeeAndMonth(
            @PathVariable Long employeeId,
            @PathVariable Integer month,
            @PathVariable Integer year) {
        List<Overtime> overtimes = overtimeService.getOvertimesByEmployeeIdAndMonth(employeeId, month, year);
        return ResponseEntity.ok(ApiResponse.success(overtimes));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOvertime(@PathVariable Long id) {
        overtimeService.deleteOvertime(id);
        return ResponseEntity.ok(ApiResponse.success("Overtime deleted successfully", null));
    }
}


