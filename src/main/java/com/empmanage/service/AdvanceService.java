package com.empmanage.service;

import com.empmanage.dto.request.AdvanceRequest;
import com.empmanage.entity.Advance;
import com.empmanage.entity.Employee;
import com.empmanage.repository.AdvanceRepository;
import com.empmanage.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdvanceService {

    private final AdvanceRepository advanceRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public Advance createAdvance(AdvanceRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + request.getEmployeeId()));

        Advance advance = new Advance();
        advance.setEmployee(employee);
        advance.setAmount(request.getAmount());
        advance.setAdvanceDate(request.getAdvanceDate());
        advance.setDescription(request.getDescription());

        return advanceRepository.save(advance);
    }

    public List<Advance> getAdvancesByEmployeeId(Long employeeId) {
        return advanceRepository.findByEmployeeId(employeeId);
    }

    public List<Advance> getAdvancesByEmployeeIdAndMonth(Long employeeId, Integer month, Integer year) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        return advanceRepository.findByEmployeeIdAndDateRange(employeeId, startDate, endDate);
    }
}


