package com.empmanage.repository;

import com.empmanage.entity.SalaryDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaryDetailRepository extends JpaRepository<SalaryDetail, Long> {
    List<SalaryDetail> findBySalaryId(Long salaryId);
}


