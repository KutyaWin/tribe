package com.covenant.tribe.scheduling.service;

import com.covenant.tribe.scheduling.model.BroadcastEntity;
import com.covenant.tribe.scheduling.notifications.NotificationStatus;
import com.covenant.tribe.scheduling.model.Broadcast;
import com.covenant.tribe.scheduling.model.Notification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationService {
    Notification create(Notification notification);
    Notification update(Notification notification);
    List<Notification> getMessagesForBroadcast(BroadcastEntity broadcast);

    List<Notification> getMessagesForBroadcastWithStatus(Broadcast broadcast, NotificationStatus status);

    List<Notification> createMessagesForBroadcast(BroadcastEntity broadcast);
}
