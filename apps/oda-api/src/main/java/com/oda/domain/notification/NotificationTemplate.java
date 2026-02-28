package com.oda.domain.notification;

public record NotificationTemplate(
        String titleTemplate,
        String bodyTemplate,
        NotificationType type
) {}
