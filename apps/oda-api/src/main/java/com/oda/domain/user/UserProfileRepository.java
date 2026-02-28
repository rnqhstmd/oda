package com.oda.domain.user;

import com.oda.domain.user.UserProfile;

import java.util.Optional;

public interface UserProfileRepository {
    UserProfile save(UserProfile userProfile);
    Optional<UserProfile> findByUserId(Long userId);
}
