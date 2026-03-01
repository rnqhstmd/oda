package com.oda.infrastructure.notification.persistence;

import com.oda.domain.notification.Notification;
import com.oda.domain.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaNotificationRepository implements NotificationRepository {

    private final SpringDataNotificationRepository springDataNotificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public Notification save(Notification notification) {
        NotificationJpaEntity entity = notificationMapper.toEntity(notification);
        NotificationJpaEntity saved = springDataNotificationRepository.save(entity);
        return notificationMapper.toDomain(saved);
    }

    @Override
    public Optional<Notification> findById(Long id) {
        return springDataNotificationRepository.findById(id)
                .map(notificationMapper::toDomain);
    }

    @Override
    public List<Notification> findByUserId(Long userId) {
        return springDataNotificationRepository.findByUserId(userId).stream()
                .map(notificationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByUserIdAndReadFalse(Long userId) {
        return springDataNotificationRepository.findByUserIdAndReadFalse(userId).stream()
                .map(notificationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByUserIdAndReadFalse(Long userId) {
        return springDataNotificationRepository.countByUserIdAndReadFalse(userId);
    }
}
