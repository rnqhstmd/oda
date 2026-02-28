package com.oda.application.user;

import com.oda.infrastructure.security.jwt.JwtTokenProvider;
import com.oda.application.user.dto.OAuthLoginCommand;
import com.oda.application.user.dto.OAuthLoginResult;
import com.oda.domain.user.OAuthUserInfo;
import com.oda.domain.user.RefreshToken;
import com.oda.domain.user.User;
import com.oda.domain.user.OAuthPort;
import com.oda.domain.user.RefreshTokenRepository;
import com.oda.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OAuthLoginService {

    private final OAuthPort oAuthPort;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${oda.jwt.refresh-expiration:604800000}")
    private long refreshExpiration;

    public OAuthLoginResult login(OAuthLoginCommand command) {
        // 1. OAuth code → access token
        String oauthAccessToken = oAuthPort.exchangeToken(command.provider(), command.code(), command.redirectUri());

        // 2. access token → user info
        OAuthUserInfo userInfo = oAuthPort.getUserInfo(command.provider(), oauthAccessToken);

        // 3. Find or create user
        Optional<User> existingUser = userRepository.findByEmail(userInfo.email());
        boolean isNewUser = existingUser.isEmpty();

        User user;
        if (isNewUser) {
            user = User.createFromOAuth(command.provider(), userInfo.oauthId(), userInfo.email(), userInfo.name());
            user = userRepository.save(user);
        } else {
            user = existingUser.get();
            // Link OAuth if different provider
            if (!user.getOauthProvider().equals(command.provider())) {
                user.linkOAuthAccount(command.provider(), userInfo.oauthId());
                user = userRepository.save(user);
            }
        }

        // 4. Create JWT tokens
        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshTokenStr = jwtTokenProvider.createRefreshToken(user.getId());

        // 5. Save refresh token
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(refreshExpiration / 1000);
        RefreshToken refreshToken = RefreshToken.create(user.getId(), refreshTokenStr, expiresAt);
        refreshTokenRepository.save(refreshToken);

        return new OAuthLoginResult(accessToken, refreshTokenStr, isNewUser, user.getId());
    }
}
