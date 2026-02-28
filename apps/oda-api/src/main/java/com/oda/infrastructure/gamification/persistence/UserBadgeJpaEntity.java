package com.oda.infrastructure.gamification.persistence;

import com.oda.domain.gamification.Badge;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_badges",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "badge"}))
@Getter
@NoArgsConstructor
public class UserBadgeJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Badge badge;

    @Column(name = "earned_at", nullable = false)
    private LocalDateTime earnedAt;

    public UserBadgeJpaEntity(Long userId, Badge badge, LocalDateTime earnedAt) {
        this.userId = userId;
        this.badge = badge;
        this.earnedAt = earnedAt;
    }
}
