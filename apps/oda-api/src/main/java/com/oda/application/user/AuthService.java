package com.oda.application.user;

import com.oda.domain.user.TokenProvider;
import com.oda.domain.user.RefreshToken;
import com.oda.domain.user.RefreshTokenRepository;
import com.oda.support.error.CoreException;
import com.oda.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider jwtTokenProvider;

    public String refreshToken(String refreshTokenStr) {
        if (!jwtTokenProvider.validateToken(refreshTokenStr)) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Invalid or expired refresh token");
        }

        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "Refresh token not found"));

        if (storedToken.isExpired()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Refresh token has expired");
        }

        Long userId = jwtTokenProvider.getUserIdFromToken(refreshTokenStr);
        return jwtTokenProvider.createAccessToken(userId);
    }

    public void logout(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}
