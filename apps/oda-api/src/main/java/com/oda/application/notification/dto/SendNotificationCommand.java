package com.oda.application.notification.dto;

import com.oda.domain.notification.NotificationChannel;
import com.oda.domain.notification.NotificationType;

public record SendNotificationCommand(
        Long userId,
        String title,
        String body,
        NotificationType type,
        NotificationChannel channel,
        Long referenceId,
        String referenceType
) {}
