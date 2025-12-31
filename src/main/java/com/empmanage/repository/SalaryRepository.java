package com.empmanage.repository;

import com.empmanage.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {
    List<Salary> findByEmployeeIdOrderByYearDescMonthDesc(Long employeeId);
    
    Optional<Salary> findByEmployeeIdAndMonthAndYear(
            Long employeeId, 
            Integer month, 
            Integer year
    );
    
    @Query("SELECT SUM(s.finalSalary) FROM Salary s WHERE s.month = :month AND s.year = :year")
    java.math.BigDecimal getTotalSalaryForMonth(
            @Param("month") Integer month,
            @Param("year") Integer year
    );
}


