package com.covenant.tribe.scheduling.message;

import com.covenant.tribe.scheduling.model.Notification;
import com.covenant.tribe.scheduling.notifications.NotificationRepository;
import com.covenant.tribe.scheduling.notifications.NotificationStatus;
import com.covenant.tribe.service.FirebaseService;
import com.covenant.tribe.service.impl.FirebaseServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FirebaseMessageStrategy implements MessageStrategy {

    FirebaseService firebaseService;
    NotificationRepository notificationRepository;

    @Override
    public void sendNotifications(List<Notification> notifications) {
        List<String> firebaseIds = notifications.stream()
                .map(notification -> {
                    return notification.getUserById().getFirebaseId();
                })
                .toList();
        String title = "Скоро начало";
        Long eventId = notifications.get(0).getBroadcastEntity().getSubjectId();
        String messageText = notifications.get(0).getText();
        firebaseService.sendNotificationsByFirebaseIds(firebaseIds, title, messageText, eventId);
        notifications.forEach(
                notification -> notification.setStatus(NotificationStatus.SUCCESSFULLY_SENT)
        );
        notificationRepository.saveAll(notifications);
    }
    @Override
    public MessageStrategyName getStrategyName() {
        return MessageStrategyName.FIREBASE;
    }
}
