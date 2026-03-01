package com.oda.domain.gamification;

import com.oda.domain.gamification.Badge;
import com.oda.domain.gamification.UserBadge;

import java.util.List;

public interface UserBadgeRepository {
    List<UserBadge> findByUserId(Long userId);
    UserBadge save(UserBadge userBadge);
    boolean existsByUserIdAndBadge(Long userId, Badge badge);
}
