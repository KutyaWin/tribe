package com.covenant.tribe.scheduling.message;

import com.covenant.tribe.scheduling.model.Notification;
import com.covenant.tribe.scheduling.notifications.NotificationRepository;
import com.covenant.tribe.scheduling.notifications.NotificationStatus;
import com.covenant.tribe.scheduling.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class ConsoleMessageStrategy implements MessageStrategy {

    private final NotificationService notificationService;

    @Override
    public void sendNotifications(List<Notification> notifications) {
        notifications.forEach(n-> {
            System.out.println("sending notification to console: " + n.getText());
            n.setStatus(NotificationStatus.SUCCESSFULLY_SENT);

        });
    }

    @Override
    public MessageStrategyName getStrategyName() {
        return null;
    }
}
