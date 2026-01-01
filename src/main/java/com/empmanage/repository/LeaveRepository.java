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
           "AND l.leaveDate BETWEEN :startDate AND :endDate " +
           "ORDER BY l.leaveDate")
    List<Leave> findByEmployeeIdAndDateRange(
            @Param("employeeId") Long employeeId,
            @Param("startDate") java.time.LocalDate startDate,
            @Param("endDate") java.time.LocalDate endDate
    );
    
    @Query("SELECT l FROM Leave l WHERE l.employee.id = :employeeId " +
           "AND l.leaveDate BETWEEN :startDate AND :endDate " +
           "AND l.leaveType = 'UNPAID' " +
           "ORDER BY l.leaveDate")
    List<Leave> findUnpaidLeavesByEmployeeIdAndDateRange(
            @Param("employeeId") Long employeeId,
            @Param("startDate") java.time.LocalDate startDate,
            @Param("endDate") java.time.LocalDate endDate
    );
}


