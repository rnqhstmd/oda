package com.oda.infrastructure.user.persistence;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "educations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EducationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String schoolName;
    private String major;
    private String degree;
    private Boolean graduated;

    public static EducationJpaEntity create(String schoolName, String major, String degree, Boolean graduated) {
        EducationJpaEntity entity = new EducationJpaEntity();
        entity.schoolName = schoolName;
        entity.major = major;
        entity.degree = degree;
        entity.graduated = graduated;
        return entity;
    }
}
