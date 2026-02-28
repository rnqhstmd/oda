package com.oda.interfaces.api.gamification.dto;

import java.time.LocalDateTime;

public record BadgeResponse(String badge, LocalDateTime earnedAt) {}
