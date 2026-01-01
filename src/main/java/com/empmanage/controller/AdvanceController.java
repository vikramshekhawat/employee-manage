package com.empmanage.controller;

import com.empmanage.dto.request.AdvanceRequest;
import com.empmanage.dto.response.ApiResponse;
import com.empmanage.entity.Advance;
import com.empmanage.service.AdvanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/advances")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdvanceController {

    private final AdvanceService advanceService;

    @PostMapping
    public ResponseEntity<ApiResponse<Advance>> createAdvance(@Valid @RequestBody AdvanceRequest request) {
        Advance advance = advanceService.createAdvance(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Advance created successfully", advance));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<List<Advance>>> getAdvancesByEmployee(@PathVariable Long employeeId) {
        List<Advance> advances = advanceService.getAdvancesByEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.success(advances));
    }

    @GetMapping("/employee/{employeeId}/month/{month}/year/{year}")
    public ResponseEntity<ApiResponse<List<Advance>>> getAdvancesByEmployeeAndMonth(
            @PathVariable Long employeeId,
            @PathVariable Integer month,
            @PathVariable Integer year) {
        List<Advance> advances = advanceService.getAdvancesByEmployeeIdAndMonth(employeeId, month, year);
        return ResponseEntity.ok(ApiResponse.success(advances));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAdvance(@PathVariable Long id) {
        advanceService.deleteAdvance(id);
        return ResponseEntity.ok(ApiResponse.success("Advance deleted successfully", null));
    }
}


