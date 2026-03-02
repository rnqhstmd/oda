package com.oda.application.user;

import com.oda.domain.user.TokenProvider;
import com.oda.domain.user.RefreshToken;
import com.oda.domain.user.RefreshTokenRepository;
import com.oda.support.error.CoreException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private TokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

    @Test
    void 토큰_갱신_성공() {
        // given
        String refreshTokenStr = "valid-refresh-token";
        Long userId = 1L;
        RefreshToken storedToken = RefreshToken.create(userId, refreshTokenStr, LocalDateTime.now().plusDays(7));

        given(tokenProvider.validateToken(refreshTokenStr)).willReturn(true);
        given(refreshTokenRepository.findByToken(refreshTokenStr)).willReturn(Optional.of(storedToken));
        given(tokenProvider.getUserIdFromToken(refreshTokenStr)).willReturn(userId);
        given(tokenProvider.createAccessToken(userId)).willReturn("new-access-token");

        // when
        String newAccessToken = authService.refreshToken(refreshTokenStr);

        // then
        assertThat(newAccessToken).isEqualTo("new-access-token");
    }

    @Test
    void 만료된_리프레시_토큰_갱신_실패() {
        // given
        String expiredToken = "expired-refresh-token";
        RefreshToken storedToken = RefreshToken.create(1L, expiredToken, LocalDateTime.now().minusDays(1));

        given(tokenProvider.validateToken(expiredToken)).willReturn(true);
        given(refreshTokenRepository.findByToken(expiredToken)).willReturn(Optional.of(storedToken));

        // when & then
        assertThatThrownBy(() -> authService.refreshToken(expiredToken))
                .isInstanceOf(CoreException.class)
                .hasMessageContaining("expired");
    }

    @Test
    void 로그아웃_리프레시_토큰_삭제() {
        // given
        Long userId = 1L;

        // when
        authService.logout(userId);

        // then
        verify(refreshTokenRepository).deleteByUserId(userId);
    }
}
