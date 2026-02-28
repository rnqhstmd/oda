package com.oda.application.gamification.dto;

import java.time.LocalDateTime;

public record BadgeResult(String badge, LocalDateTime earnedAt) {}
