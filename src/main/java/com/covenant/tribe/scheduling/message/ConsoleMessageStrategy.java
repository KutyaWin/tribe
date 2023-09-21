package com.covenant.tribe.scheduling.message;

import com.covenant.tribe.scheduling.model.Notification;
import com.covenant.tribe.scheduling.notifications.NotificationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsoleMessageStrategy implements MessageStrategy {

    @Override
    public void sendNotifications(List<Notification> notifications) {
        notifications.forEach(n-> {
            System.out.println("sending notification to console: " + n.getText());
            n.setStatus(NotificationStatus.SUCCESSFULLY_SENT);
        });
    }

    @Override
    public MessageStrategyName getStrategyName() {
        return MessageStrategyName.CONSOLE;
    }
}
