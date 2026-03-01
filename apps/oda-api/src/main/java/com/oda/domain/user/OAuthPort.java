package com.oda.domain.user;

import com.oda.domain.user.OAuthProvider;
import com.oda.domain.user.OAuthUserInfo;

public interface OAuthPort {
    String exchangeToken(OAuthProvider provider, String code, String redirectUri);
    OAuthUserInfo getUserInfo(OAuthProvider provider, String accessToken);
}
