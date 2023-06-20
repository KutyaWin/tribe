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

import java.util.ArrayList;
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
        Preconditions.checkState(notification.getId() == null,
                "New notification has not null id");
        return notificationRepository.save(notification);
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
    @Transactional(readOnly = true)
    public List<Notification> getMessagesForBroadcast(BroadcastEntity broadcastEntity) {
        List<Notification> notificationsById = new ArrayList<>();
        if (broadcastEntity.getNotificationsCreated()) {
            notificationRepository.findAllByBroadcastEntity(broadcastEntity);
        }
        return notificationsById;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getMessagesForBroadcastWithStatus(Broadcast broadcast, NotificationStatus status) {
        return null;
    }


    @Override
    @Transactional
    public List<Notification> createMessagesForBroadcast(BroadcastEntity broadcast) {
        NotificationStrategy notificationStrategy = notificationsStrategyFactory.find(broadcast.getNotificationStrategyName());
        return notificationStrategy.getNotifications(broadcast.getId());
    }

    private Notification findById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(String.format(
                        "Notification with id %s didn't found", id)));
    }
}
