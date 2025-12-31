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
           "AND MONTH(o.overtimeDate) = :month AND YEAR(o.overtimeDate) = :year " +
           "ORDER BY o.overtimeDate")
    List<Overtime> findByEmployeeIdAndMonthAndYear(
            @Param("employeeId") Long employeeId,
            @Param("month") Integer month,
            @Param("year") Integer year
    );
}


