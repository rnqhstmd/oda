package com.oda.application.user.dto;

import com.oda.domain.user.OAuthProvider;

public record OAuthLoginCommand(OAuthProvider provider, String code, String redirectUri) {}
