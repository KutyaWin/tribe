package com.covenant.tribe.scheduling.notifications;

import com.covenant.tribe.scheduling.model.BroadcastEntity;
import com.covenant.tribe.scheduling.model.Notification;
import com.covenant.tribe.scheduling.model.QBroadcastEntity;

import java.util.List;


public interface NotificationStrategy {
    List<Notification> getNotifications(BroadcastEntity broadcast);

    NotificationStrategyName getStrategyName();
}
