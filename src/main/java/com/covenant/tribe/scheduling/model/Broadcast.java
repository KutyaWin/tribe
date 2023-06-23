package com.covenant.tribe.scheduling.model;

import com.covenant.tribe.scheduling.BroadcastStatuses;
import com.covenant.tribe.scheduling.message.MessageStrategyName;
import com.covenant.tribe.scheduling.notifications.NotificationStrategyName;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.OffsetDateTime;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter

public class Broadcast implements Serializable {

    Long broadcastEntityId;

    @NotNull
    Long subjectId;

    BroadcastStatuses status = BroadcastStatuses.NEW;

    @NotNull
    OffsetDateTime repeatDate;

    @NotNull
    OffsetDateTime endDate;

    Integer retryRateSeconds = 2;

    @NotNull
    NotificationStrategyName notificationStrategyName;

    MessageStrategyName messageStrategyName = MessageStrategyName.CONSOLE;

    public Broadcast(Long subjectId, OffsetDateTime repeatDate, OffsetDateTime endDate, NotificationStrategyName notificationStrategyName) {
        this.subjectId = subjectId;
        this.repeatDate = repeatDate;
        this.endDate = endDate;
        this.notificationStrategyName = notificationStrategyName;
    }

    public Broadcast(Long subjectId, BroadcastStatuses status, OffsetDateTime repeatDate, OffsetDateTime endDate, Integer retryRateSeconds, NotificationStrategyName notificationStrategyName, MessageStrategyName messageStrategyName) {
        this.subjectId = subjectId;
        this.status = status;
        this.repeatDate = repeatDate;
        this.endDate = endDate;
        this.retryRateSeconds = retryRateSeconds;
        this.notificationStrategyName = notificationStrategyName;
        this.messageStrategyName = messageStrategyName;
    }
}
