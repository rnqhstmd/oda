package com.oda.infrastructure.gamification.persistence;

import com.oda.domain.gamification.UserBadge;
import org.springframework.stereotype.Component;

@Component
public class UserBadgeMapper {

    public UserBadge toDomain(UserBadgeJpaEntity entity) {
        return UserBadge.reconstruct(
                entity.getId(),
                entity.getUserId(),
                entity.getBadge(),
                entity.getEarnedAt()
        );
    }

    public UserBadgeJpaEntity toJpa(UserBadge domain) {
        return new UserBadgeJpaEntity(
                domain.getUserId(),
                domain.getBadge(),
                domain.getEarnedAt()
        );
    }
}
