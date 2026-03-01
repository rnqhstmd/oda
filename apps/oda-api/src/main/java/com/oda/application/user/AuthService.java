package com.oda.application.user;

import com.oda.infrastructure.security.jwt.JwtTokenProvider;
import com.oda.domain.user.RefreshToken;
import com.oda.domain.user.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public String refreshToken(String refreshTokenStr) {
        if (!jwtTokenProvider.validateToken(refreshTokenStr)) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }

        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));

        if (storedToken.isExpired()) {
            throw new IllegalArgumentException("Refresh token has expired");
        }

        Long userId = jwtTokenProvider.getUserIdFromToken(refreshTokenStr);
        return jwtTokenProvider.createAccessToken(userId);
    }

    public void logout(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}
