package com.oda.infrastructure.gamification.persistence;

import com.oda.domain.gamification.UserStreak;
import org.springframework.stereotype.Component;

@Component
public class UserStreakMapper {

    public UserStreak toDomain(UserStreakJpaEntity entity) {
        return UserStreak.reconstruct(
                entity.getId(),
                entity.getUserId(),
                entity.getCurrentStreak(),
                entity.getLongestStreak(),
                entity.getLastActiveDate()
        );
    }

    public UserStreakJpaEntity toJpa(UserStreak domain) {
        return new UserStreakJpaEntity(
                domain.getUserId(),
                domain.getCurrentStreak(),
                domain.getLongestStreak(),
                domain.getLastActiveDate()
        );
    }
}
