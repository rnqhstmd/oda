package com.oda.infrastructure.user.oauth;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.oda.domain.user.OAuthUserInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@WireMockTest(httpPort = 8090)
class GoogleOAuthAdapterTest {

    private GoogleOAuthAdapter googleOAuthAdapter;

    @BeforeEach
    void setUp() {
        googleOAuthAdapter = new GoogleOAuthAdapter(
                "http://localhost:8090/token",
                "http://localhost:8090/oauth2/v3/userinfo",
                "test-google-client-id",
                "test-google-client-secret"
        );
    }

    @Test
    @DisplayName("코드로 토큰 교환 성공")
    void 코드로_토큰_교환_성공() {
        stubFor(post(urlEqualTo("/token"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "access_token": "google-access-token",
                                  "token_type": "Bearer",
                                  "expires_in": 3599,
                                  "id_token": "google-id-token"
                                }
                                """)));

        String token = googleOAuthAdapter.exchangeCodeForToken("google-auth-code", "http://localhost/callback");

        assertThat(token).isEqualTo("google-access-token");
        verify(postRequestedFor(urlEqualTo("/token")));
    }

    @Test
    @DisplayName("토큰으로 사용자 정보 조회")
    void 토큰으로_사용자정보_조회() {
        stubFor(get(urlEqualTo("/oauth2/v3/userinfo"))
                .withHeader("Authorization", equalTo("Bearer google-access-token"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "sub": "google-user-123",
                                  "email": "google@example.com",
                                  "name": "구글유저",
                                  "picture": "https://profile.img/google.jpg"
                                }
                                """)));

        OAuthUserInfo userInfo = googleOAuthAdapter.getUserInfo("google-access-token");

        assertThat(userInfo.oauthId()).isEqualTo("google-user-123");
        assertThat(userInfo.email()).isEqualTo("google@example.com");
        assertThat(userInfo.name()).isEqualTo("구글유저");
        assertThat(userInfo.profileImageUrl()).isEqualTo("https://profile.img/google.jpg");
    }

    @Test
    @DisplayName("API 오류 응답 예외 처리")
    void API_오류_응답_예외처리() {
        stubFor(get(urlEqualTo("/oauth2/v3/userinfo"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "error": "invalid_token",
                                  "error_description": "Token has been expired or revoked."
                                }
                                """)));

        assertThatThrownBy(() -> googleOAuthAdapter.getUserInfo("expired-token"))
                .isInstanceOf(Exception.class);
    }
}
