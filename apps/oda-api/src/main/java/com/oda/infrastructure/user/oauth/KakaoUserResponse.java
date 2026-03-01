package com.oda.infrastructure.user.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserResponse(
        String id,
        @JsonProperty("kakao_account") KakaoAccount kakaoAccount
) {
    public record KakaoAccount(
            String email,
            @JsonProperty("profile") KakaoProfile profile
    ) {}

    public record KakaoProfile(
            String nickname,
            @JsonProperty("profile_image_url") String profileImageUrl
    ) {}
}
