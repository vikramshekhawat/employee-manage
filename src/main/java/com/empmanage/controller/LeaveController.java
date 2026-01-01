package com.empmanage.controller;

import com.empmanage.dto.request.LeaveRequest;
import com.empmanage.dto.response.ApiResponse;
import com.empmanage.entity.Leave;
import com.empmanage.service.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LeaveController {

    private final LeaveService leaveService;

    @PostMapping
    public ResponseEntity<ApiResponse<Leave>> createLeave(@Valid @RequestBody LeaveRequest request) {
        Leave leave = leaveService.createLeave(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Leave created successfully", leave));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<List<Leave>>> getLeavesByEmployee(@PathVariable Long employeeId) {
        List<Leave> leaves = leaveService.getLeavesByEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.success(leaves));
    }

    @GetMapping("/employee/{employeeId}/month/{month}/year/{year}")
    public ResponseEntity<ApiResponse<List<Leave>>> getLeavesByEmployeeAndMonth(
            @PathVariable Long employeeId,
            @PathVariable Integer month,
            @PathVariable Integer year) {
        List<Leave> leaves = leaveService.getLeavesByEmployeeIdAndMonth(employeeId, month, year);
        return ResponseEntity.ok(ApiResponse.success(leaves));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLeave(@PathVariable Long id) {
        leaveService.deleteLeave(id);
        return ResponseEntity.ok(ApiResponse.success("Leave deleted successfully", null));
    }
}


