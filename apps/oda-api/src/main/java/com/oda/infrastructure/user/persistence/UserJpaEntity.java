package com.oda.infrastructure.user.persistence;

import com.oda.domain.common.BaseEntity;
import com.oda.domain.user.OAuthProvider;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private OAuthProvider oauthProvider;

    private String oauthId;

    private String passwordHash;

    private boolean consentPersonalInfo;

    private boolean consentSensitiveInfo;

    public static UserJpaEntity create(String email, String name, OAuthProvider oauthProvider,
                                       String oauthId, String passwordHash,
                                       boolean consentPersonalInfo, boolean consentSensitiveInfo) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.email = email;
        entity.name = name;
        entity.oauthProvider = oauthProvider;
        entity.oauthId = oauthId;
        entity.passwordHash = passwordHash;
        entity.consentPersonalInfo = consentPersonalInfo;
        entity.consentSensitiveInfo = consentSensitiveInfo;
        return entity;
    }

    public void update(String name, OAuthProvider oauthProvider, String oauthId,
                       boolean consentPersonalInfo, boolean consentSensitiveInfo) {
        this.name = name;
        this.oauthProvider = oauthProvider;
        this.oauthId = oauthId;
        this.consentPersonalInfo = consentPersonalInfo;
        this.consentSensitiveInfo = consentSensitiveInfo;
    }
}
