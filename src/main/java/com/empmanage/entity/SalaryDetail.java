package com.empmanage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "salary_details")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SalaryDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salary_id", nullable = false)
    @ToString.Exclude
    private Salary salary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DetailType type;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(length = 500)
    private String description;

    public enum DetailType {
        OVERTIME, ADVANCE, LEAVE
    }
}


