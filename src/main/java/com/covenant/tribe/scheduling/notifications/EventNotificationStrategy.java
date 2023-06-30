package com.covenant.tribe.scheduling.notifications;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.scheduling.model.BroadcastEntity;
import com.covenant.tribe.scheduling.model.Notification;
import com.covenant.tribe.service.EventService;
import com.covenant.tribe.service.UserRelationsWithEventService;
import com.covenant.tribe.service.UserService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class EventNotificationStrategy implements NotificationStrategy {

    EventService eventService;
    UserRelationsWithEventService userRelationsWithEventService;

    @Override
    public List<Notification> getNotifications(BroadcastEntity broadcast) {

        List<User> eventParticipants = userRelationsWithEventService
                .findParticipantsByEventId(broadcast.getSubjectId());
        List<Notification> notifications = new ArrayList<>();
        Event eventById = eventService.getEventById(broadcast.getSubjectId());
        String eventName = eventById.getEventName();
        for (int i = 0; i < 2; i++) {
            User user = eventParticipants.get(i);
            Notification notification =
                    new Notification("notification for event name: " +
                            eventName, user, broadcast);
            notifications.add(notification);
        }
        return notifications;
    }

    @Override
    public NotificationStrategyName getStrategyName() {
        return NotificationStrategyName.EVENT;
    }
}
