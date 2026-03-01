package com.oda.domain.user;

import com.oda.domain.common.BaseEntity;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RefreshToken extends BaseEntity {
    private Long id;
    private Long userId;
    private String token;
    private LocalDateTime expiresAt;

    private RefreshToken() {}

    public static RefreshToken create(Long userId, String token, LocalDateTime expiresAt) {
        RefreshToken rt = new RefreshToken();
        rt.userId = userId;
        rt.token = token;
        rt.expiresAt = expiresAt;
        return rt;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
