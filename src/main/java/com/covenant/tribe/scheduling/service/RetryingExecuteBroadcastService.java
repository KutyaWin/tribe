package com.covenant.tribe.scheduling.service;

import com.covenant.tribe.scheduling.BroadcastStatuses;
import com.covenant.tribe.scheduling.message.MessageStrategy;
import com.covenant.tribe.scheduling.message.MessageStrategyFactory;
import com.covenant.tribe.scheduling.model.BroadcastEntity;
import com.covenant.tribe.scheduling.notifications.NotificationStatus;
import com.covenant.tribe.scheduling.model.Broadcast;
import com.covenant.tribe.scheduling.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "scheduler.mode", havingValue = "retrying")
public class RetryingExecuteBroadcastService implements ExecuteBroadcastService{

    private final NotificationService notificationService;

    private final BroadcastService broadcastService;

    private final MessageStrategyFactory messageStrategyFactory;
    private final Integer repeatRate = 1;
    @Override
    @Transactional
    public void executeBroadcast(Broadcast broadcast)  {
        BroadcastEntity byId = broadcastService.findById(broadcast.getBroadcastEntityId());

        BroadcastStatuses status = byId.getStatus();
        try {
            byId.setFireCount(byId.getFireCount() + 1);
            if (status.equals(BroadcastStatuses.NEW)) {
                execForNew(byId);
            } else if (status.equals(BroadcastStatuses.IN_PROGRESS)) {
                execForInProgress(byId);
            }
        } catch (RuntimeException e) {
            byId.setStatus(BroadcastStatuses.ENDED_WITH_ERROR);
            broadcastService.update(byId);
            String message = String.format(
                    "Broadcast ended with error: %s", e.getMessage()
            );
            log.error(message);
        }
        broadcast.setRepeatDate(LocalDateTime.now().plusSeconds(repeatRate));

    }

    private void execForNew(BroadcastEntity broadcast) {
        List<Notification> messagesForBroadcast = notificationService.createNotificationsForBroadcast(broadcast);
        broadcast.setStatus(BroadcastStatuses.IN_PROGRESS);
        executeForMessages(messagesForBroadcast, broadcast);
        broadcast.setStatus(BroadcastStatuses.COMPLETE_SUCCESSFULLY);
    }

    private void execForInProgress(BroadcastEntity broadcast) {
        List<Notification> messagesForBroadcast = notificationService
                .getMessagesForBroadcastWithStatus(broadcast, NotificationStatus.NEW);
        if (messagesForBroadcast.isEmpty()) {
            broadcast.setStatus(BroadcastStatuses.COMPLETE_SUCCESSFULLY);
        } else {
            executeForMessages(messagesForBroadcast, broadcast);
        }
    }

    private void executeForMessages(List<Notification> messagesForBroadcast, BroadcastEntity broadcast) {
        MessageStrategy messageStrategy = messageStrategyFactory.find(broadcast.getMessageStrategyName());
        messageStrategy.sendNotifications(messagesForBroadcast);
    }
}
