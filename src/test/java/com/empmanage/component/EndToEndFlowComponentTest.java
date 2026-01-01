package com.empmanage.component;

import com.empmanage.dto.request.*;
import com.empmanage.entity.Employee;
import com.empmanage.entity.Salary;
import com.empmanage.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("End-to-End Complete Flow Component Test")
class EndToEndFlowComponentTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AdvanceRepository advanceRepository;

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private OvertimeRepository overtimeRepository;

    @Autowired
    private SalaryRepository salaryRepository;

    @BeforeEach
    void setUp() {
        salaryRepository.deleteAll();
        advanceRepository.deleteAll();
        leaveRepository.deleteAll();
        overtimeRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    @Test
    @DisplayName("Complete E2E Flow: Login -> Create Employee -> Add Transactions -> Calculate Salary -> View Dashboard")
    void testCompleteEndToEndFlow() throws Exception {
        // Step 1: Login
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("admin123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Step 2: Create Employee
        EmployeeRequest employeeRequest = new EmployeeRequest();
        employeeRequest.setName("John Doe");
        employeeRequest.setMobile("9876543210");
        employeeRequest.setBaseSalary(new BigDecimal("50000"));
        employeeRequest.setPfPercentage(new BigDecimal("12"));

        String employeeResponse = mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract employee ID (simplified - in real scenario use proper parsing)
        Employee savedEmployee = employeeRepository.findByMobile("9876543210").orElseThrow();
        Long employeeId = savedEmployee.getId();

        // Step 3: Add Overtime
        OvertimeRequest overtimeRequest = new OvertimeRequest();
        overtimeRequest.setEmployeeId(employeeId);
        overtimeRequest.setOvertimeDate(LocalDate.now().withDayOfMonth(5));
        overtimeRequest.setHours(new BigDecimal("4"));
        overtimeRequest.setRatePerHour(new BigDecimal("500"));

        mockMvc.perform(post("/api/overtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overtimeRequest)))
                .andExpect(status().isCreated());

        // Step 4: Add Advance
        AdvanceRequest advanceRequest = new AdvanceRequest();
        advanceRequest.setEmployeeId(employeeId);
        advanceRequest.setAmount(new BigDecimal("5000"));
        advanceRequest.setAdvanceDate(LocalDate.now().withDayOfMonth(10));
        advanceRequest.setDescription("Emergency advance");

        mockMvc.perform(post("/api/advances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(advanceRequest)))
                .andExpect(status().isCreated());

        // Step 5: Add Unpaid Leave
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployeeId(employeeId);
        leaveRequest.setLeaveDate(LocalDate.now().withDayOfMonth(15));
        leaveRequest.setLeaveType(com.empmanage.entity.Leave.LeaveType.UNPAID);
        leaveRequest.setDescription("Sick leave");

        mockMvc.perform(post("/api/leaves")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(leaveRequest)))
                .andExpect(status().isCreated());

        // Step 6: Preview Salary
        LocalDate now = LocalDate.now();
        SalaryGenerationRequest salaryRequest = new SalaryGenerationRequest();
        salaryRequest.setEmployeeId(employeeId);
        salaryRequest.setMonth(now.getMonthValue());
        salaryRequest.setYear(now.getYear());

        mockMvc.perform(post("/api/salary/preview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(salaryRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.finalSalary").exists());

        // Step 7: Generate Salary
        mockMvc.perform(post("/api/salary/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(salaryRequest)))
                .andExpect(status().isCreated());

        // Verify salary is saved
        assertTrue(salaryRepository.findByEmployeeIdAndMonthAndYear(
                employeeId, now.getMonthValue(), now.getYear()).isPresent());

        // Step 8: Get Salary History
        mockMvc.perform(get("/api/salary/employee/{employeeId}", employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1));

        // Step 9: View Dashboard
        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalEmployees").value(1))
                .andExpect(jsonPath("$.data.activeEmployees").value(1));

        // Step 10: Get All Employees
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));

        // Step 11: Get Monthly Transactions
        mockMvc.perform(get("/api/advances/employee/{employeeId}/month/{month}/year/{year}",
                        employeeId, now.getMonthValue(), now.getYear()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));

        mockMvc.perform(get("/api/overtimes/employee/{employeeId}/month/{month}/year/{year}",
                        employeeId, now.getMonthValue(), now.getYear()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));

        mockMvc.perform(get("/api/leaves/employee/{employeeId}/month/{month}/year/{year}",
                        employeeId, now.getMonthValue(), now.getYear()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }
}

