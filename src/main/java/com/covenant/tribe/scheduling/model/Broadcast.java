package com.covenant.tribe.scheduling.model;

import com.covenant.tribe.scheduling.BroadcastStatuses;
import com.covenant.tribe.scheduling.message.MessageStrategyName;
import com.covenant.tribe.scheduling.notifications.NotificationStrategyName;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
public class Broadcast implements Serializable {

    Long broadcastEntityId;

    Long subjectId;

    String triggerKey;

    BroadcastStatuses status = BroadcastStatuses.NEW;

    LocalDateTime repeatDate;

    LocalDateTime endDate;

    Integer retryRateSeconds = 2;

    NotificationStrategyName notificationStrategyName;

    MessageStrategyName messageStrategyName = MessageStrategyName.CONSOLE;

    public Broadcast(Long subjectId, LocalDateTime repeatDate, LocalDateTime endDate, NotificationStrategyName notificationStrategyName) {
        this.subjectId = subjectId;
        this.repeatDate = repeatDate;
        this.endDate = endDate;
        this.notificationStrategyName = notificationStrategyName;
    }

    public Broadcast(Long subjectId, BroadcastStatuses status, LocalDateTime repeatDate, LocalDateTime endDate, Integer retryRateSeconds, NotificationStrategyName notificationStrategyName, MessageStrategyName messageStrategyName) {
        this.subjectId = subjectId;
        this.status = status;
        this.repeatDate = repeatDate;
        this.endDate = endDate;
        this.retryRateSeconds = retryRateSeconds;
        this.notificationStrategyName = notificationStrategyName;
        this.messageStrategyName = messageStrategyName;
    }
}
