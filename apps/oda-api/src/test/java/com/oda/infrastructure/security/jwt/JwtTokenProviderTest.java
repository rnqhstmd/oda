package com.oda.infrastructure.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    // Use a test secret (at least 256 bits / 32 bytes, base64 encoded)
    private static final String TEST_SECRET = "dGVzdFNlY3JldEtleUZvckp3dFRva2VuUHJvdmlkZXJUZXN0MTIzNDU2Nzg5MA==";
    private static final long ACCESS_EXPIRATION = 1800000L; // 30 min
    private static final long REFRESH_EXPIRATION = 1209600000L; // 14 days

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(TEST_SECRET, ACCESS_EXPIRATION, REFRESH_EXPIRATION);
    }

    @Test
    void 액세스_토큰_생성_및_검증() {
        String token = jwtTokenProvider.createAccessToken(1L);
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
    }

    @Test
    void 리프레시_토큰_생성_및_검증() {
        String token = jwtTokenProvider.createRefreshToken(1L);
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
    }

    @Test
    void 만료된_토큰_검증_실패() {
        JwtTokenProvider shortLived = new JwtTokenProvider(TEST_SECRET, 0L, 0L);
        String token = shortLived.createAccessToken(1L);
        // Small delay to ensure expiration
        try { Thread.sleep(10); } catch (InterruptedException ignored) {}
        assertThat(shortLived.validateToken(token)).isFalse();
    }

    @Test
    void 잘못된_토큰_검증_실패() {
        assertThat(jwtTokenProvider.validateToken("invalid.token.value")).isFalse();
    }

    @Test
    void 토큰에서_사용자ID_추출() {
        Long userId = 42L;
        String token = jwtTokenProvider.createAccessToken(userId);
        assertThat(jwtTokenProvider.getUserIdFromToken(token)).isEqualTo(userId);
    }

    @Test
    void null_토큰_검증_실패() {
        assertThat(jwtTokenProvider.validateToken(null)).isFalse();
    }

    @Test
    void 빈_문자열_토큰_검증_실패() {
        assertThat(jwtTokenProvider.validateToken("")).isFalse();
    }
}
