package com.oda.interfaces.api.user.dto;

public record OAuthLoginResponse(String accessToken, String refreshToken, boolean isNewUser) {}
