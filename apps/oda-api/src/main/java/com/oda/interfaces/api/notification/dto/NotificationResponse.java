package com.oda.interfaces.api.notification.dto;

import com.oda.application.notification.dto.NotificationResult;
import com.oda.domain.notification.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        String title,
        String body,
        NotificationType type,
        boolean read,
        LocalDateTime createdAt
) {
    public static NotificationResponse from(NotificationResult result) {
        return new NotificationResponse(
                result.id(),
                result.title(),
                result.body(),
                result.type(),
                result.read(),
                result.createdAt()
        );
    }
}
