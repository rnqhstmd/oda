package com.oda.infrastructure.gamification.persistence;

import com.oda.domain.gamification.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataUserBadgeRepository extends JpaRepository<UserBadgeJpaEntity, Long> {
    List<UserBadgeJpaEntity> findByUserId(Long userId);
    boolean existsByUserIdAndBadge(Long userId, Badge badge);
}
