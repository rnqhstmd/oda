package com.oda.infrastructure.gamification.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataUserStreakRepository extends JpaRepository<UserStreakJpaEntity, Long> {
    Optional<UserStreakJpaEntity> findByUserId(Long userId);
}
