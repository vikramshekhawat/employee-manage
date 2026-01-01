package com.empmanage.repository;

import com.empmanage.entity.Overtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OvertimeRepository extends JpaRepository<Overtime, Long> {
    List<Overtime> findByEmployeeId(Long employeeId);
    
    @Query("SELECT o FROM Overtime o WHERE o.employee.id = :employeeId " +
           "AND o.overtimeDate BETWEEN :startDate AND :endDate " +
           "ORDER BY o.overtimeDate")
    List<Overtime> findByEmployeeIdAndDateRange(
            @Param("employeeId") Long employeeId,
            @Param("startDate") java.time.LocalDate startDate,
            @Param("endDate") java.time.LocalDate endDate
    );
}


