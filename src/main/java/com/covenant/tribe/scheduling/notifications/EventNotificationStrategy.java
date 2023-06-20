package com.covenant.tribe.scheduling.notifications;

import com.covenant.tribe.scheduling.model.Notification;
import com.covenant.tribe.scheduling.notifications.NotificationStrategy;
import com.covenant.tribe.scheduling.notifications.NotificationStrategyName;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventNotificationStrategy implements NotificationStrategy {


    @Override
    public List<Notification> getNotifications(Long subjectId) {
        return null;
    }

    @Override
    public NotificationStrategyName getStrategyName() {
        return NotificationStrategyName.EVENT;
    }
}
