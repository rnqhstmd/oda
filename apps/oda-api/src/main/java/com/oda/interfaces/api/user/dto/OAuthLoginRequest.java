package com.oda.interfaces.api.user.dto;

public record OAuthLoginRequest(String code, String redirectUri) {}
