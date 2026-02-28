package com.oda.infrastructure.notification.persistence;

import com.oda.domain.notification.NotificationChannel;
import com.oda.domain.notification.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataNotificationPreferenceRepository extends JpaRepository<NotificationPreferenceJpaEntity, Long> {
    List<NotificationPreferenceJpaEntity> findByUserId(Long userId);
    Optional<NotificationPreferenceJpaEntity> findByUserIdAndTypeAndChannel(Long userId, NotificationType type, NotificationChannel channel);
}
