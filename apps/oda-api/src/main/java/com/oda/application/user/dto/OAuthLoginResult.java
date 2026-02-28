package com.oda.application.user.dto;

public record OAuthLoginResult(String accessToken, String refreshToken, boolean isNewUser, Long userId) {}
