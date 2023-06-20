package com.covenant.tribe.scheduling.message;

import com.covenant.tribe.scheduling.model.Notification;

import java.util.List;


public interface MessageStrategy {
    void sendNotifications(List<Notification> notifications);

    MessageStrategyName getStrategyName();
}
