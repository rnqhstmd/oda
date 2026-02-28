package com.oda.interfaces.api.user.dto;

import com.oda.domain.user.OAuthProvider;
import com.oda.domain.user.User;

public record UserResponse(
        Long id,
        String email,
        String name,
        OAuthProvider provider,
        boolean consentPersonalInfo,
        boolean consentSensitiveInfo
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getOauthProvider(),
                user.isConsentPersonalInfo(),
                user.isConsentSensitiveInfo()
        );
    }
}
