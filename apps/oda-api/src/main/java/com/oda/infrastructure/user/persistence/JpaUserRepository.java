package com.oda.infrastructure.user.persistence;

import com.oda.domain.user.OAuthProvider;
import com.oda.domain.user.User;
import com.oda.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, Long> {
    Optional<UserJpaEntity> findByEmail(String email);
    Optional<UserJpaEntity> findByOauthProviderAndOauthId(OAuthProvider provider, String oauthId);
    boolean existsByEmail(String email);
}

@Repository
@RequiredArgsConstructor
public class JpaUserRepository implements UserRepository {

    private final SpringDataUserRepository springRepo;
    private final UserMapper mapper;

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            UserJpaEntity entity = mapper.toEntity(user);
            UserJpaEntity saved = springRepo.save(entity);
            return mapper.toDomain(saved);
        }
        UserJpaEntity existing = springRepo.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + user.getId()));
        existing.update(user.getName(), user.getOauthProvider(), user.getOauthId(),
                user.isConsentPersonalInfo(), user.isConsentSensitiveInfo());
        return mapper.toDomain(springRepo.save(existing));
    }

    @Override
    public Optional<User> findById(Long id) {
        return springRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return springRepo.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByOauthProviderAndOauthId(OAuthProvider provider, String oauthId) {
        return springRepo.findByOauthProviderAndOauthId(provider, oauthId).map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return springRepo.existsByEmail(email);
    }

    @Override
    public void deleteById(Long id) {
        springRepo.deleteById(id);
    }
}
