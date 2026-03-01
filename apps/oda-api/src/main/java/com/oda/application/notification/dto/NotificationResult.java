package com.oda.application.notification.dto;

import com.oda.domain.notification.NotificationType;

import java.time.LocalDateTime;

public record NotificationResult(
        Long id,
        String title,
        String body,
        NotificationType type,
        boolean read,
        LocalDateTime createdAt
) {}
