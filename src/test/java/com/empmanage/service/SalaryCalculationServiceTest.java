package com.empmanage.service;

import com.empmanage.dto.response.SalaryPreviewResponse;
import com.empmanage.entity.*;
import com.empmanage.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SalaryCalculationServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private AdvanceRepository advanceRepository;
    @Mock
    private LeaveRepository leaveRepository;
    @Mock
    private OvertimeRepository overtimeRepository;
    @Mock
    private SalaryRepository salaryRepository;
    @Mock
    private SalaryDetailRepository salaryDetailRepository;

    @InjectMocks
    private SalaryCalculationService salaryCalculationService;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setName("Test Employee");
        employee.setMobile("1234567890");
        employee.setBaseSalary(new BigDecimal("30000.00")); // 1000 per day for 30-day month
        employee.setPfPercentage(new BigDecimal("10.00"));
        employee.setActive(true);
    }

    @Test
    void testPreviewSalary_NoDeductions() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(overtimeRepository.findByEmployeeIdAndDateRange(anyLong(), any(), any())).thenReturn(Collections.emptyList());
        when(advanceRepository.findByEmployeeIdAndDateRange(anyLong(), any(), any())).thenReturn(Collections.emptyList());
        when(leaveRepository.findUnpaidLeavesByEmployeeIdAndDateRange(anyLong(), any(), any())).thenReturn(Collections.emptyList());

        SalaryPreviewResponse response = salaryCalculationService.previewSalary(1L, 11, 2023); // Nov 2023 has 30 days

        assertNotNull(response);
        assertEquals(new BigDecimal("30000.00"), response.getBaseSalary());
        assertEquals(new BigDecimal("3000.00"), response.getPfDeduction()); // 10% of 30000
        assertEquals(BigDecimal.ZERO, response.getTotalAdvances());
        assertEquals(BigDecimal.ZERO, response.getTotalOvertime());
        assertEquals(new BigDecimal("27000.00"), response.getFinalSalary());
    }
}
