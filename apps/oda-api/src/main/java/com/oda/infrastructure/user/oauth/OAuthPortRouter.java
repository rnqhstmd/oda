package com.oda.infrastructure.user.oauth;

import com.oda.domain.user.OAuthProvider;
import com.oda.domain.user.OAuthUserInfo;
import com.oda.domain.user.OAuthPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuthPortRouter implements OAuthPort {

    private final KakaoOAuthAdapter kakaoAdapter;
    private final GoogleOAuthAdapter googleAdapter;

    @Override
    public String exchangeToken(OAuthProvider provider, String code, String redirectUri) {
        return switch (provider) {
            case KAKAO -> kakaoAdapter.exchangeCodeForToken(code, redirectUri);
            case GOOGLE -> googleAdapter.exchangeCodeForToken(code, redirectUri);
        };
    }

    @Override
    public OAuthUserInfo getUserInfo(OAuthProvider provider, String accessToken) {
        return switch (provider) {
            case KAKAO -> kakaoAdapter.getUserInfo(accessToken);
            case GOOGLE -> googleAdapter.getUserInfo(accessToken);
        };
    }
}
