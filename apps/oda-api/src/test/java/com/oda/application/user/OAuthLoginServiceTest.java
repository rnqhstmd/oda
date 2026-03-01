package com.oda.application.user;

import com.oda.infrastructure.security.jwt.JwtTokenProvider;
import com.oda.application.user.dto.OAuthLoginCommand;
import com.oda.application.user.dto.OAuthLoginResult;
import com.oda.domain.user.OAuthProvider;
import com.oda.domain.user.OAuthUserInfo;
import com.oda.domain.user.RefreshToken;
import com.oda.domain.user.User;
import com.oda.domain.user.OAuthPort;
import com.oda.domain.user.RefreshTokenRepository;
import com.oda.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuthLoginServiceTest {

    @Mock private OAuthPort oAuthPort;
    @Mock private UserRepository userRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private OAuthLoginService oAuthLoginService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(oAuthLoginService, "refreshExpiration", 604800000L);
    }

    @Test
    void 신규_사용자_소셜로그인_isNewUser_true() {
        // given
        OAuthLoginCommand command = new OAuthLoginCommand(OAuthProvider.KAKAO, "auth-code", "http://redirect");
        OAuthUserInfo userInfo = new OAuthUserInfo("kakao-123", "new@example.com", "홍길동", null);

        given(oAuthPort.exchangeToken(OAuthProvider.KAKAO, "auth-code", "http://redirect")).willReturn("oauth-access-token");
        given(oAuthPort.getUserInfo(OAuthProvider.KAKAO, "oauth-access-token")).willReturn(userInfo);
        given(userRepository.findByEmail("new@example.com")).willReturn(Optional.empty());

        User savedUser = User.createFromOAuth(OAuthProvider.KAKAO, "kakao-123", "new@example.com", "홍길동");
        ReflectionTestUtils.setField(savedUser, "id", 1L);
        given(userRepository.save(any(User.class))).willReturn(savedUser);
        given(jwtTokenProvider.createAccessToken(1L)).willReturn("access-token");
        given(jwtTokenProvider.createRefreshToken(1L)).willReturn("refresh-token");
        given(refreshTokenRepository.save(any(RefreshToken.class))).willReturn(
                RefreshToken.create(1L, "refresh-token", LocalDateTime.now().plusDays(7)));

        // when
        OAuthLoginResult result = oAuthLoginService.login(command);

        // then
        assertThat(result.isNewUser()).isTrue();
        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshToken()).isEqualTo("refresh-token");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void 기존_사용자_소셜로그인_isNewUser_false() {
        // given
        OAuthLoginCommand command = new OAuthLoginCommand(OAuthProvider.KAKAO, "auth-code", "http://redirect");
        OAuthUserInfo userInfo = new OAuthUserInfo("kakao-123", "existing@example.com", "기존유저", null);

        User existingUser = User.createFromOAuth(OAuthProvider.KAKAO, "kakao-123", "existing@example.com", "기존유저");
        ReflectionTestUtils.setField(existingUser, "id", 2L);

        given(oAuthPort.exchangeToken(OAuthProvider.KAKAO, "auth-code", "http://redirect")).willReturn("oauth-access-token");
        given(oAuthPort.getUserInfo(OAuthProvider.KAKAO, "oauth-access-token")).willReturn(userInfo);
        given(userRepository.findByEmail("existing@example.com")).willReturn(Optional.of(existingUser));
        given(jwtTokenProvider.createAccessToken(2L)).willReturn("access-token-2");
        given(jwtTokenProvider.createRefreshToken(2L)).willReturn("refresh-token-2");
        given(refreshTokenRepository.save(any(RefreshToken.class))).willReturn(
                RefreshToken.create(2L, "refresh-token-2", LocalDateTime.now().plusDays(7)));

        // when
        OAuthLoginResult result = oAuthLoginService.login(command);

        // then
        assertThat(result.isNewUser()).isFalse();
        assertThat(result.userId()).isEqualTo(2L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void 다른_Provider_동일_이메일_계정_연동() {
        // given: 기존 KAKAO User, 이번에 GOOGLE로 같은 이메일 로그인
        OAuthLoginCommand command = new OAuthLoginCommand(OAuthProvider.GOOGLE, "google-code", "http://redirect");
        OAuthUserInfo userInfo = new OAuthUserInfo("google-456", "shared@example.com", "공유유저", null);

        User existingUser = User.createFromOAuth(OAuthProvider.KAKAO, "kakao-123", "shared@example.com", "공유유저");
        ReflectionTestUtils.setField(existingUser, "id", 3L);

        given(oAuthPort.exchangeToken(OAuthProvider.GOOGLE, "google-code", "http://redirect")).willReturn("google-access-token");
        given(oAuthPort.getUserInfo(OAuthProvider.GOOGLE, "google-access-token")).willReturn(userInfo);
        given(userRepository.findByEmail("shared@example.com")).willReturn(Optional.of(existingUser));
        given(userRepository.save(existingUser)).willReturn(existingUser);
        given(jwtTokenProvider.createAccessToken(3L)).willReturn("access-token-3");
        given(jwtTokenProvider.createRefreshToken(3L)).willReturn("refresh-token-3");
        given(refreshTokenRepository.save(any(RefreshToken.class))).willReturn(
                RefreshToken.create(3L, "refresh-token-3", LocalDateTime.now().plusDays(7)));

        // when
        OAuthLoginResult result = oAuthLoginService.login(command);

        // then: 기존 유저에 Google 계정 연동
        assertThat(result.isNewUser()).isFalse();
        assertThat(existingUser.getOauthProvider()).isEqualTo(OAuthProvider.GOOGLE);
        verify(userRepository).save(existingUser);
    }
}
