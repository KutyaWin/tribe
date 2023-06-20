package com.covenant.tribe.scheduling.service;

import com.covenant.tribe.scheduling.BroadcastStatuses;
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
@ConditionalOnProperty(name = "scheduler.enabled", havingValue = "false")
public class RetryingExecuteBroadcastService implements ExecuteBroadcastService{

    private final NotificationService notificationService;

    private final BroadcastService broadcastService;
    private final Integer repeatRate = 1;
    @Override
    @Transactional
    public void executeBroadcast(Broadcast broadcast) throws SchedulerException {
        BroadcastEntity byId = broadcastService.findById(broadcast.getBroadcastEntityId());
        BroadcastStatuses status = byId.getStatus();
        if (!status.equals(BroadcastStatuses.COMPLETE_SUCCESSFULLY)) {
            if (status.equals(BroadcastStatuses.NEW)) {
                execForNew(broadcast);
            } else if (status.equals(BroadcastStatuses.IN_PROGRESS)) {
                execForInProgress(broadcast);
            }
            broadcast.setRepeatDate(OffsetDateTime.now().plus(repeatRate, ChronoUnit.SECONDS));
        }
    }

    private void execForNew(Broadcast broadcast) {
        BroadcastEntity byId = broadcastService.findById(broadcast.getBroadcastEntityId());
        List<Notification> messagesForBroadcast = notificationService.getMessagesForBroadcast(byId);

        byId.setStatus(BroadcastStatuses.IN_PROGRESS);
        executeForMessages(messagesForBroadcast);
    }

    private void execForInProgress(Broadcast broadcast) {
        BroadcastEntity byId = broadcastService.findById(broadcast.getBroadcastEntityId());
        List<Notification> messagesForBroadcast = notificationService
                .getMessagesForBroadcastWithStatus(broadcast, NotificationStatus.NEW);
        if (messagesForBroadcast.size()==0) {
            byId.setStatus(BroadcastStatuses.COMPLETE_SUCCESSFULLY);
        } else {
            executeForMessages(messagesForBroadcast);
        }
    }

    private void executeForMessages(List<Notification> messagesForBroadcast) {
        for (Notification message:messagesForBroadcast) {
        }
    }
}
