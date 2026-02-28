package com.oda.domain.notification;

import com.oda.domain.common.BaseEntity;
import lombok.Getter;

@Getter
public class Notification extends BaseEntity {

    private Long id;
    private Long userId;
    private String title;
    private String body;
    private NotificationType type;
    private NotificationChannel channel;
    private Long referenceId;
    private String referenceType;
    private boolean read;

    private Notification() {}

    public static Notification create(Long userId, String title, String body,
                                      NotificationType type, NotificationChannel channel,
                                      Long referenceId, String referenceType) {
        Notification notification = new Notification();
        notification.userId = userId;
        notification.title = title;
        notification.body = body;
        notification.type = type;
        notification.channel = channel;
        notification.referenceId = referenceId;
        notification.referenceType = referenceType;
        notification.read = false;
        return notification;
    }

    public static Notification reconstruct(Long id, Long userId, String title, String body,
                                           NotificationType type, NotificationChannel channel,
                                           Long referenceId, String referenceType, boolean read) {
        Notification notification = new Notification();
        notification.id = id;
        notification.userId = userId;
        notification.title = title;
        notification.body = body;
        notification.type = type;
        notification.channel = channel;
        notification.referenceId = referenceId;
        notification.referenceType = referenceType;
        notification.read = read;
        return notification;
    }

    public void markAsRead() {
        this.read = true;
    }
}
