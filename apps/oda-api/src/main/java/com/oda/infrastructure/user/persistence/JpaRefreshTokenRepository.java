package com.oda.infrastructure.user.persistence;

import com.oda.domain.user.RefreshToken;
import com.oda.domain.user.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

interface SpringDataRefreshTokenRepository extends JpaRepository<RefreshTokenJpaEntity, Long> {
    Optional<RefreshTokenJpaEntity> findByToken(String token);
    Optional<RefreshTokenJpaEntity> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}

@Repository
@RequiredArgsConstructor
public class JpaRefreshTokenRepository implements RefreshTokenRepository {

    private final SpringDataRefreshTokenRepository springRepo;

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        // Delete existing token for this user before saving new one
        springRepo.deleteByUserId(refreshToken.getUserId());
        RefreshTokenJpaEntity entity = RefreshTokenJpaEntity.create(
                refreshToken.getUserId(), refreshToken.getToken(), refreshToken.getExpiresAt());
        RefreshTokenJpaEntity saved = springRepo.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return springRepo.findByToken(token).map(this::toDomain);
    }

    @Override
    public Optional<RefreshToken> findByUserId(Long userId) {
        return springRepo.findByUserId(userId).map(this::toDomain);
    }

    @Override
    public void deleteByUserId(Long userId) {
        springRepo.deleteByUserId(userId);
    }

    private RefreshToken toDomain(RefreshTokenJpaEntity entity) {
        return RefreshToken.create(entity.getUserId(), entity.getToken(), entity.getExpiresAt());
    }
}
