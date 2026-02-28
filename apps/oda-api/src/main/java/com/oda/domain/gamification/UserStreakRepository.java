package com.oda.domain.gamification;

import com.oda.domain.gamification.UserStreak;

import java.util.Optional;

public interface UserStreakRepository {
    Optional<UserStreak> findByUserId(Long userId);
    UserStreak save(UserStreak userStreak);
}
