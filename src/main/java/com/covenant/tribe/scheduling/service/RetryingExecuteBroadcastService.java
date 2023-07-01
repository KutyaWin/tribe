package com.covenant.tribe.scheduling.service;

import com.covenant.tribe.scheduling.BroadcastStatuses;
import com.covenant.tribe.scheduling.message.MessageStrategy;
import com.covenant.tribe.scheduling.message.MessageStrategyFactory;
import com.covenant.tribe.scheduling.model.BroadcastEntity;
import com.covenant.tribe.scheduling.notifications.NotificationStatus;
import com.covenant.tribe.scheduling.model.Broadcast;
import com.covenant.tribe.scheduling.model.Notification;
import lombok.RequiredArgsConstructor;
import org.quartz.SchedulerException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "scheduler.mode", havingValue = "retrying")
public class RetryingExecuteBroadcastService implements ExecuteBroadcastService{

    private final NotificationService notificationService;

    private final BroadcastService broadcastService;

    private final MessageStrategyFactory messageStrategyFactory;
    private final Integer repeatRate = 1;
    @Override
    @Transactional
    public void executeBroadcast(Broadcast broadcast) throws SchedulerException {
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
            throw new SchedulerException("Broadcast ended with error");
        }
        broadcast.setRepeatDate(OffsetDateTime.now().plus(repeatRate, ChronoUnit.SECONDS));

    }

    private void execForNew(BroadcastEntity broadcast) {
        List<Notification> messagesForBroadcast = notificationService.createNotificationsForBroadcast(broadcast);
        broadcast.setStatus(BroadcastStatuses.IN_PROGRESS); //TODO Почему in_progress нигде не меняется на successfully
        executeForMessages(messagesForBroadcast, broadcast);
        broadcast.setStatus(BroadcastStatuses.COMPLETE_SUCCESSFULLY);
    }

    private void execForInProgress(BroadcastEntity broadcast) {
        List<Notification> messagesForBroadcast = notificationService
                .getMessagesForBroadcastWithStatus(broadcast, NotificationStatus.NEW);
        if (messagesForBroadcast.size()==0) {
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
