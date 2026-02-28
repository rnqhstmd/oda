package com.oda.domain.user;

import com.oda.domain.common.BaseEntity;
import lombok.Getter;

@Getter
public class User extends BaseEntity {
    private Long id;
    private String email;
    private String name;
    private OAuthProvider oauthProvider;
    private String oauthId;
    private String passwordHash;
    private boolean consentPersonalInfo;
    private boolean consentSensitiveInfo;

    private User() {}

    public static User createFromOAuth(OAuthProvider provider, String oauthId, String email, String name) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email must not be null or blank");
        }
        User user = new User();
        user.oauthProvider = provider;
        user.oauthId = oauthId;
        user.email = email;
        user.name = name;
        user.consentPersonalInfo = false;
        user.consentSensitiveInfo = false;
        return user;
    }

    public void updateConsent(boolean personalInfo, boolean sensitiveInfo) {
        this.consentPersonalInfo = personalInfo;
        this.consentSensitiveInfo = sensitiveInfo;
    }

    public void updateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be null or blank");
        }
        this.name = name;
    }

    public void linkOAuthAccount(OAuthProvider provider, String oauthId) {
        this.oauthProvider = provider;
        this.oauthId = oauthId;
    }
}
