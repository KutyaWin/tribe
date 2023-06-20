package com.covenant.tribe.scheduling.notifications;

import com.covenant.tribe.scheduling.model.Notification;

import java.util.List;


public interface NotificationStrategy {
    List<Notification> getNotifications(Long subjectId);

    NotificationStrategyName getStrategyName();
}
