package com.oda.domain.user;

import com.oda.domain.user.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    Optional<User> findByOauthProviderAndOauthId(com.oda.domain.user.OAuthProvider provider, String oauthId);
    boolean existsByEmail(String email);
    void deleteById(Long id);
}
