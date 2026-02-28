package com.oda.domain.gamification;

import java.time.LocalDateTime;

public class UserBadge {

    private Long id;
    private Long userId;
    private Badge badge;
    private LocalDateTime earnedAt;

    private UserBadge() {}

    public static UserBadge create(Long userId, Badge badge) {
        if (userId == null) throw new IllegalArgumentException("userId must not be null");
        if (badge == null) throw new IllegalArgumentException("badge must not be null");
        UserBadge userBadge = new UserBadge();
        userBadge.userId = userId;
        userBadge.badge = badge;
        userBadge.earnedAt = LocalDateTime.now();
        return userBadge;
    }

    public static UserBadge reconstruct(Long id, Long userId, Badge badge, LocalDateTime earnedAt) {
        UserBadge userBadge = new UserBadge();
        userBadge.id = id;
        userBadge.userId = userId;
        userBadge.badge = badge;
        userBadge.earnedAt = earnedAt;
        return userBadge;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Badge getBadge() { return badge; }
    public LocalDateTime getEarnedAt() { return earnedAt; }
}
