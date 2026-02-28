package com.oda.infrastructure.gamification.persistence;

import com.oda.domain.gamification.UserStreak;
import com.oda.domain.gamification.UserStreakRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaUserStreakRepository implements UserStreakRepository {

    private final SpringDataUserStreakRepository springDataRepository;
    private final UserStreakMapper mapper;

    public JpaUserStreakRepository(SpringDataUserStreakRepository springDataRepository,
                                    UserStreakMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<UserStreak> findByUserId(Long userId) {
        return springDataRepository.findByUserId(userId)
                .map(mapper::toDomain);
    }

    @Override
    public UserStreak save(UserStreak userStreak) {
        Optional<UserStreakJpaEntity> existing = springDataRepository.findByUserId(userStreak.getUserId());
        UserStreakJpaEntity entity;
        if (existing.isPresent()) {
            entity = existing.get();
            entity.update(userStreak.getCurrentStreak(), userStreak.getLongestStreak(), userStreak.getLastActiveDate());
        } else {
            entity = mapper.toJpa(userStreak);
        }
        UserStreakJpaEntity saved = springDataRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
