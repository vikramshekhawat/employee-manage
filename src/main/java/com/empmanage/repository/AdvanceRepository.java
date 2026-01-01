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
           "AND a.advanceDate BETWEEN :startDate AND :endDate " +
           "ORDER BY a.advanceDate")
    List<Advance> findByEmployeeIdAndDateRange(
            @Param("employeeId") Long employeeId,
            @Param("startDate") java.time.LocalDate startDate,
            @Param("endDate") java.time.LocalDate endDate
    );
}


