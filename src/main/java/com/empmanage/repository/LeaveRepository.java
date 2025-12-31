package com.empmanage.repository;

import com.empmanage.entity.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {
    List<Leave> findByEmployeeId(Long employeeId);
    
    @Query("SELECT l FROM Leave l WHERE l.employee.id = :employeeId " +
           "AND MONTH(l.leaveDate) = :month AND YEAR(l.leaveDate) = :year " +
           "ORDER BY l.leaveDate")
    List<Leave> findByEmployeeIdAndMonthAndYear(
            @Param("employeeId") Long employeeId,
            @Param("month") Integer month,
            @Param("year") Integer year
    );
    
    @Query("SELECT l FROM Leave l WHERE l.employee.id = :employeeId " +
           "AND MONTH(l.leaveDate) = :month AND YEAR(l.leaveDate) = :year " +
           "AND l.leaveType = 'UNPAID' " +
           "ORDER BY l.leaveDate")
    List<Leave> findUnpaidLeavesByEmployeeIdAndMonthAndYear(
            @Param("employeeId") Long employeeId,
            @Param("month") Integer month,
            @Param("year") Integer year
    );
}


