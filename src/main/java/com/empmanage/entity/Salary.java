package com.empmanage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "salaries", uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "month", "year"}))
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    @ToString.Exclude
    private Employee employee;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal baseSalary;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalOvertime;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAdvances;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalLeaves;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pfDeduction;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal finalSalary;

    @Column(nullable = false)
    private Boolean smsSent = false;

    private LocalDateTime smsSentAt;
}


