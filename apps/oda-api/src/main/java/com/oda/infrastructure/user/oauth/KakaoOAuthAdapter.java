package com.oda.infrastructure.user.oauth;

import com.oda.domain.user.OAuthProvider;
import com.oda.domain.user.OAuthUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class KakaoOAuthAdapter {

    private final RestClient restClient;
    private final String tokenUrl;
    private final String userInfoUrl;
    private final String clientId;
    private final String clientSecret;

    public KakaoOAuthAdapter(
            @Value("${oda.oauth.kakao.token-url:https://kauth.kakao.com/oauth/token}") String tokenUrl,
            @Value("${oda.oauth.kakao.user-info-url:https://kapi.kakao.com/v2/user/me}") String userInfoUrl,
            @Value("${oda.oauth.kakao.client-id:}") String clientId,
            @Value("${oda.oauth.kakao.client-secret:}") String clientSecret) {
        this.tokenUrl = tokenUrl;
        this.userInfoUrl = userInfoUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.restClient = RestClient.create();
    }

    // Constructor for testing with custom RestClient
    KakaoOAuthAdapter(String tokenUrl, String userInfoUrl, String clientId, String clientSecret, RestClient restClient) {
        this.tokenUrl = tokenUrl;
        this.userInfoUrl = userInfoUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.restClient = restClient;
    }

    public String exchangeCodeForToken(String code, String redirectUri) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);
        if (clientSecret != null && !clientSecret.isBlank()) {
            params.add("client_secret", clientSecret);
        }

        KakaoTokenResponse response = restClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(params)
                .retrieve()
                .body(KakaoTokenResponse.class);

        if (response == null || response.accessToken() == null) {
            throw new RuntimeException("Failed to exchange Kakao authorization code for token");
        }
        return response.accessToken();
    }

    public OAuthUserInfo getUserInfo(String accessToken) {
        KakaoUserResponse response = restClient.get()
                .uri(userInfoUrl)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(KakaoUserResponse.class);

        if (response == null) {
            throw new RuntimeException("Failed to get Kakao user info");
        }

        String email = response.kakaoAccount() != null ? response.kakaoAccount().email() : null;
        String name = null;
        String profileImageUrl = null;
        if (response.kakaoAccount() != null && response.kakaoAccount().profile() != null) {
            name = response.kakaoAccount().profile().nickname();
            profileImageUrl = response.kakaoAccount().profile().profileImageUrl();
        }

        return new OAuthUserInfo(String.valueOf(response.id()), email, name, profileImageUrl);
    }
}
