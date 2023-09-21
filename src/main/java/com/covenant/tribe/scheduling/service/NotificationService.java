package com.covenant.tribe.scheduling.service;

import com.covenant.tribe.scheduling.model.BroadcastEntity;
import com.covenant.tribe.scheduling.notifications.NotificationStatus;
import com.covenant.tribe.scheduling.model.Broadcast;
import com.covenant.tribe.scheduling.model.Notification;

import java.util.List;

public interface NotificationService {
    Notification create(Notification notification);

    List<Notification> create(List<Notification> notification);
    Notification update(Notification notification);

    List<Notification> getMessagesForBroadcast(Broadcast broadcast);

    List<Notification> getMessagesForBroadcastEntity(BroadcastEntity broadcast);

    List<Notification> getMessagesForBroadcastWithStatus(BroadcastEntity broadcast, NotificationStatus status);

    List<Notification> createNotificationsForBroadcast(BroadcastEntity broadcast);
}
