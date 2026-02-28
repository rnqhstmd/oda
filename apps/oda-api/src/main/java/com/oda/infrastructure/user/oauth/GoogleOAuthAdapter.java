package com.oda.infrastructure.user.oauth;

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
public class GoogleOAuthAdapter {

    private final RestClient restClient;
    private final String tokenUrl;
    private final String userInfoUrl;
    private final String clientId;
    private final String clientSecret;

    public GoogleOAuthAdapter(
            @Value("${oda.oauth.google.token-url:https://oauth2.googleapis.com/token}") String tokenUrl,
            @Value("${oda.oauth.google.user-info-url:https://www.googleapis.com/oauth2/v3/userinfo}") String userInfoUrl,
            @Value("${oda.oauth.google.client-id:}") String clientId,
            @Value("${oda.oauth.google.client-secret:}") String clientSecret) {
        this.tokenUrl = tokenUrl;
        this.userInfoUrl = userInfoUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.restClient = RestClient.create();
    }

    // Constructor for testing with custom URLs and RestClient
    GoogleOAuthAdapter(String tokenUrl, String userInfoUrl, String clientId, String clientSecret, RestClient restClient) {
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
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        GoogleTokenResponse response = restClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(params)
                .retrieve()
                .body(GoogleTokenResponse.class);

        if (response == null || response.accessToken() == null) {
            throw new RuntimeException("Failed to exchange Google authorization code for token");
        }
        return response.accessToken();
    }

    public OAuthUserInfo getUserInfo(String accessToken) {
        GoogleUserResponse response = restClient.get()
                .uri(userInfoUrl)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(GoogleUserResponse.class);

        if (response == null) {
            throw new RuntimeException("Failed to get Google user info");
        }

        return new OAuthUserInfo(response.sub(), response.email(), response.name(), response.profileImageUrl());
    }
}
