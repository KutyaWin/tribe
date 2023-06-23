package com.covenant.tribe.scheduling.notifications;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.scheduling.model.BroadcastEntity;
import com.covenant.tribe.scheduling.model.Notification;
import com.covenant.tribe.scheduling.model.QBroadcastEntity;
import com.covenant.tribe.scheduling.notifications.NotificationStrategy;
import com.covenant.tribe.scheduling.notifications.NotificationStrategyName;
import com.covenant.tribe.service.EventService;
import com.covenant.tribe.service.UserService;
import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventNotificationStrategy implements NotificationStrategy {

    private final UserService userService;

    private final EventService eventService;

    @Override
    public List<Notification> getNotifications(BroadcastEntity entity) {
//        TODO::add logic to find users associated to event
        List<User> all = userService.findAll();
        List<Notification> notifications = new ArrayList<>();
        Event eventById = eventService.getEventById(entity.getSubjectId());
        String eventName = eventById.getEventName();
        for (int i = 0; i < 2; i++) {
            User user = all.get(i);
            Notification notification =
                    new Notification("notification for event name: " +
                            eventName, user, entity);
            notifications.add(notification);
        }
        return notifications;
    }

    @Override
    public NotificationStrategyName getStrategyName() {
        return NotificationStrategyName.EVENT;
    }
}
