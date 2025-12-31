package com.empmanage.repository;

import com.empmanage.entity.Advance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvanceRepository extends JpaRepository<Advance, Long> {
    List<Advance> findByEmployeeId(Long employeeId);
    
    @Query("SELECT a FROM Advance a WHERE a.employee.id = :employeeId " +
           "AND MONTH(a.advanceDate) = :month AND YEAR(a.advanceDate) = :year " +
           "ORDER BY a.advanceDate")
    List<Advance> findByEmployeeIdAndMonthAndYear(
            @Param("employeeId") Long employeeId,
            @Param("month") Integer month,
            @Param("year") Integer year
    );
}


