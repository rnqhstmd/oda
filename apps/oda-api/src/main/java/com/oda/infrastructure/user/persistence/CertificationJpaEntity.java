package com.oda.infrastructure.user.persistence;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "certifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CertificationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String issuer;
    private LocalDate acquiredDate;

    public static CertificationJpaEntity create(String name, String issuer, LocalDate acquiredDate) {
        CertificationJpaEntity entity = new CertificationJpaEntity();
        entity.name = name;
        entity.issuer = issuer;
        entity.acquiredDate = acquiredDate;
        return entity;
    }
}
