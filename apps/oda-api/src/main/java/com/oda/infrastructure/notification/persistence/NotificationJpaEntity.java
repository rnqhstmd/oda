package com.oda.infrastructure.notification.persistence;

import com.oda.domain.common.BaseEntity;
import com.oda.domain.notification.NotificationChannel;
import com.oda.domain.notification.NotificationType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    private Long referenceId;

    private String referenceType;

    @Column(name = "is_read", nullable = false)
    private boolean read;

    public static NotificationJpaEntity create(Long userId, String title, String body,
                                               NotificationType type, NotificationChannel channel,
                                               Long referenceId, String referenceType, boolean read) {
        NotificationJpaEntity entity = new NotificationJpaEntity();
        entity.userId = userId;
        entity.title = title;
        entity.body = body;
        entity.type = type;
        entity.channel = channel;
        entity.referenceId = referenceId;
        entity.referenceType = referenceType;
        entity.read = read;
        return entity;
    }

    public void markAsRead() {
        this.read = true;
    }
}
