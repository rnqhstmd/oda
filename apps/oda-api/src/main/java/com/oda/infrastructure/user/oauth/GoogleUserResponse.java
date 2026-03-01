package com.oda.infrastructure.user.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleUserResponse(
        String sub,
        String email,
        String name,
        @JsonProperty("picture") String profileImageUrl
) {}
