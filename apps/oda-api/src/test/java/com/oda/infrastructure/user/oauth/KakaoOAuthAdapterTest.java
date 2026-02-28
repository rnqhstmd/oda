package com.oda.infrastructure.user.oauth;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.oda.domain.user.OAuthUserInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@WireMockTest(httpPort = 8089)
class KakaoOAuthAdapterTest {

    private KakaoOAuthAdapter kakaoOAuthAdapter;

    @BeforeEach
    void setUp() {
        kakaoOAuthAdapter = new KakaoOAuthAdapter(
                "http://localhost:8089/oauth/token",
                "http://localhost:8089/v2/user/me",
                "test-client-id",
                "test-client-secret"
        );
    }

    @Test
    @DisplayName("코드로 토큰 교환 성공")
    void 코드로_토큰_교환_성공() {
        stubFor(post(urlEqualTo("/oauth/token"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "access_token": "test-access-token",
                                  "token_type": "bearer",
                                  "refresh_token": "test-refresh-token",
                                  "expires_in": 21599
                                }
                                """)));

        String token = kakaoOAuthAdapter.exchangeCodeForToken("auth-code-123", "http://localhost/callback");

        assertThat(token).isEqualTo("test-access-token");
        verify(postRequestedFor(urlEqualTo("/oauth/token")));
    }

    @Test
    @DisplayName("토큰으로 사용자 정보 조회")
    void 토큰으로_사용자정보_조회() {
        stubFor(get(urlEqualTo("/v2/user/me"))
                .withHeader("Authorization", equalTo("Bearer test-access-token"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "id": "12345",
                                  "kakao_account": {
                                    "email": "kakao@example.com",
                                    "profile": {
                                      "nickname": "카카오유저",
                                      "profile_image_url": "https://profile.img/kakao.jpg"
                                    }
                                  }
                                }
                                """)));

        OAuthUserInfo userInfo = kakaoOAuthAdapter.getUserInfo("test-access-token");

        assertThat(userInfo.oauthId()).isEqualTo("12345");
        assertThat(userInfo.email()).isEqualTo("kakao@example.com");
        assertThat(userInfo.name()).isEqualTo("카카오유저");
        assertThat(userInfo.profileImageUrl()).isEqualTo("https://profile.img/kakao.jpg");
    }

    @Test
    @DisplayName("API 오류 응답 예외 처리")
    void API_오류_응답_예외처리() {
        stubFor(get(urlEqualTo("/v2/user/me"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "msg": "this access token does not exist",
                                  "code": -401
                                }
                                """)));

        assertThatThrownBy(() -> kakaoOAuthAdapter.getUserInfo("invalid-token"))
                .isInstanceOf(Exception.class);
    }
}
