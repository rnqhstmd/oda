package com.oda.infrastructure.gamification.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "user_streaks",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id"}))
@Getter
@NoArgsConstructor
public class UserStreakJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "current_streak", nullable = false)
    private int currentStreak;

    @Column(name = "longest_streak", nullable = false)
    private int longestStreak;

    @Column(name = "last_active_date")
    private LocalDate lastActiveDate;

    public UserStreakJpaEntity(Long userId, int currentStreak, int longestStreak, LocalDate lastActiveDate) {
        this.userId = userId;
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
        this.lastActiveDate = lastActiveDate;
    }

    public void update(int currentStreak, int longestStreak, LocalDate lastActiveDate) {
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
        this.lastActiveDate = lastActiveDate;
    }
}
