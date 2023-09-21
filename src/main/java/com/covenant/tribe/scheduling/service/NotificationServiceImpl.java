package com.covenant.tribe.scheduling.service;

import com.covenant.tribe.exeption.scheduling.NotificationNotFoundException;
import com.covenant.tribe.scheduling.model.BroadcastEntity;
import com.covenant.tribe.scheduling.notifications.*;
import com.covenant.tribe.scheduling.model.Broadcast;
import com.covenant.tribe.scheduling.model.Notification;
import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    private final BroadcastService broadcastService;

    private final NotificationsStrategyFactory notificationsStrategyFactory;

    @Override
    @Transactional
    public Notification create(Notification notification) {
        checkIdNull(notification);
        return notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public List<Notification> create(List<Notification> notification) {
        notification.forEach(this::checkIdNull);
        return notificationRepository.saveAll(notification);
    }

    private void checkIdNull(Notification notification) {
        Preconditions.checkState(notification.getId() == null,
                "New notification has not null id");
    }

    @Override
    @Transactional
    public Notification update(Notification notification) {
        Long id = notification.getId();
        Notification byId = findById(id);
        byId = notification;
        return notificationRepository.save(byId);
    }

    @Override
    @Transactional
    public List<Notification> getMessagesForBroadcast(Broadcast broadcast) {
        BroadcastEntity byId = broadcastService.findById(broadcast.getBroadcastEntityId());
        return notificationRepository.findAllByBroadcastEntity(byId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getMessagesForBroadcastEntity(BroadcastEntity broadcastEntity) {
        return notificationRepository.findAllByBroadcastEntity(broadcastEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getMessagesForBroadcastWithStatus(BroadcastEntity broadcast, NotificationStatus status) {
        return null;
    }

    @Override
    @Transactional
    public List<Notification> createNotificationsForBroadcast(BroadcastEntity broadcast) {
        NotificationStrategy notificationStrategy = notificationsStrategyFactory.find(broadcast.getNotificationStrategyName());
        List<Notification> notifications = notificationStrategy.getNotifications(broadcast);
        Preconditions.checkState(!notifications.isEmpty(),
                String.format("Cannot create any notification for broadcastEntity: %s", broadcast.getId()));
        return create(notifications);
    }

    private Notification findById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(String.format(
                        "Notification with id %s didn't found", id)));
    }
}
