package com.oda.infrastructure.user.persistence;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "work_experiences")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkExperienceJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;
    private String position;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isCurrent;

    public static WorkExperienceJpaEntity create(String companyName, String position,
                                                  LocalDate startDate, LocalDate endDate,
                                                  boolean isCurrent) {
        WorkExperienceJpaEntity entity = new WorkExperienceJpaEntity();
        entity.companyName = companyName;
        entity.position = position;
        entity.startDate = startDate;
        entity.endDate = endDate;
        entity.isCurrent = isCurrent;
        return entity;
    }
}
