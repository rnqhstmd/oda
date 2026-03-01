package com.oda.domain.gamification;

import java.time.LocalDate;

public class UserStreak {

    private Long id;
    private Long userId;
    private int currentStreak;
    private int longestStreak;
    private LocalDate lastActiveDate;

    private UserStreak() {}

    public static UserStreak create(Long userId) {
        if (userId == null) throw new IllegalArgumentException("userId must not be null");
        UserStreak streak = new UserStreak();
        streak.userId = userId;
        streak.currentStreak = 0;
        streak.longestStreak = 0;
        streak.lastActiveDate = null;
        return streak;
    }

    public static UserStreak reconstruct(Long id, Long userId, int currentStreak, int longestStreak, LocalDate lastActiveDate) {
        UserStreak streak = new UserStreak();
        streak.id = id;
        streak.userId = userId;
        streak.currentStreak = currentStreak;
        streak.longestStreak = longestStreak;
        streak.lastActiveDate = lastActiveDate;
        return streak;
    }

    public void recordActivity(LocalDate today) {
        if (lastActiveDate == null) {
            currentStreak = 1;
        } else if (lastActiveDate.equals(today)) {
            return; // no-op: same day
        } else if (lastActiveDate.equals(today.minusDays(1))) {
            currentStreak++;
        } else {
            currentStreak = 1;
        }
        if (currentStreak > longestStreak) {
            longestStreak = currentStreak;
        }
        lastActiveDate = today;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public int getCurrentStreak() { return currentStreak; }
    public int getLongestStreak() { return longestStreak; }
    public LocalDate getLastActiveDate() { return lastActiveDate; }
}
