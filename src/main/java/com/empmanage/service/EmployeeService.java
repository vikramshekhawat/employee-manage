package com.empmanage.service;

import com.empmanage.dto.request.EmployeeRequest;
import com.empmanage.dto.response.EmployeeResponse;
import com.empmanage.entity.Employee;
import com.empmanage.exception.ResourceNotFoundException;
import com.empmanage.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        if (employeeRepository.existsByMobile(request.getMobile())) {
            throw new IllegalArgumentException("Employee with mobile number " + request.getMobile() + " already exists");
        }

        Employee employee = new Employee();
        employee.setName(request.getName());
        employee.setMobile(request.getMobile());
        employee.setBaseSalary(request.getBaseSalary());
        employee.setPfAmount(request.getPfAmount());
        employee.setActive(true);

        Employee saved = employeeRepository.save(employee);
        return mapToResponse(saved);
    }

    public List<EmployeeResponse> getAllActiveEmployees() {
        return employeeRepository.findByActiveTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        return mapToResponse(employee);
    }

    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        // Check if mobile number is being changed and if it already exists
        if (!employee.getMobile().equals(request.getMobile()) && 
            employeeRepository.existsByMobile(request.getMobile())) {
            throw new IllegalArgumentException("Employee with mobile number " + request.getMobile() + " already exists");
        }

        employee.setName(request.getName());
        employee.setMobile(request.getMobile());
        employee.setBaseSalary(request.getBaseSalary());
        employee.setPfAmount(request.getPfAmount());

        Employee updated = employeeRepository.save(employee);
        return mapToResponse(updated);
    }

    @Transactional
    public void deactivateEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        employee.setActive(false);
        employeeRepository.save(employee);
    }

    public Employee getEmployeeEntity(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    private EmployeeResponse mapToResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getMobile(),
                employee.getBaseSalary(),
                employee.getPfAmount(),
                employee.getActive(),
                employee.getCreatedAt(),
                employee.getUpdatedAt()
        );
    }
}


