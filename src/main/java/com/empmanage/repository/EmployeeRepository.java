package com.empmanage.repository;

import com.empmanage.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByMobile(String mobile);
    List<Employee> findByActiveTrue();
    boolean existsByMobile(String mobile);
}


