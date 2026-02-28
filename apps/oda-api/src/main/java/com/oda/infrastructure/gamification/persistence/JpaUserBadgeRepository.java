package com.oda.infrastructure.gamification.persistence;

import com.oda.domain.gamification.Badge;
import com.oda.domain.gamification.UserBadge;
import com.oda.domain.gamification.UserBadgeRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaUserBadgeRepository implements UserBadgeRepository {

    private final SpringDataUserBadgeRepository springDataRepository;
    private final UserBadgeMapper mapper;

    public JpaUserBadgeRepository(SpringDataUserBadgeRepository springDataRepository,
                                   UserBadgeMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public List<UserBadge> findByUserId(Long userId) {
        return springDataRepository.findByUserId(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public UserBadge save(UserBadge userBadge) {
        UserBadgeJpaEntity entity = mapper.toJpa(userBadge);
        UserBadgeJpaEntity saved = springDataRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public boolean existsByUserIdAndBadge(Long userId, Badge badge) {
        return springDataRepository.existsByUserIdAndBadge(userId, badge);
    }
}
