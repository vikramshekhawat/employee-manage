package com.empmanage.service;

import com.empmanage.dto.request.OvertimeRequest;
import com.empmanage.entity.Overtime;
import com.empmanage.entity.Employee;
import com.empmanage.repository.OvertimeRepository;
import com.empmanage.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OvertimeService {

    private final OvertimeRepository overtimeRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public Overtime createOvertime(OvertimeRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + request.getEmployeeId()));

        Overtime overtime = new Overtime();
        overtime.setEmployee(employee);
        overtime.setOvertimeDate(request.getOvertimeDate());
        overtime.setHours(request.getHours());
        overtime.setRatePerHour(request.getRatePerHour());
        
        // Calculate total amount
        BigDecimal totalAmount = request.getHours().multiply(request.getRatePerHour());
        overtime.setTotalAmount(totalAmount);

        return overtimeRepository.save(overtime);
    }

    public List<Overtime> getOvertimesByEmployeeId(Long employeeId) {
        return overtimeRepository.findByEmployeeId(employeeId);
    }

    public List<Overtime> getOvertimesByEmployeeIdAndMonth(Long employeeId, Integer month, Integer year) {
        return overtimeRepository.findByEmployeeIdAndMonthAndYear(employeeId, month, year);
    }

    @Transactional
    public void deleteOvertime(Long id) {
        if (!overtimeRepository.existsById(id)) {
            throw new IllegalArgumentException("Overtime not found with id: " + id);
        }
        overtimeRepository.deleteById(id);
    }
}


