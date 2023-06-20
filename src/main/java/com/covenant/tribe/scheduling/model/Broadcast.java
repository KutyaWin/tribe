package com.covenant.tribe.scheduling.model;

import com.covenant.tribe.scheduling.BroadcastStatuses;
import com.covenant.tribe.scheduling.message.MessageStrategyName;
import com.covenant.tribe.scheduling.notifications.NotificationStrategyName;
import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Broadcast implements Serializable {

    Long broadcastEntityId;

    Long subjectId;

    BroadcastStatuses status;

    OffsetDateTime repeatDate;

    OffsetDateTime endDate;

    Integer retryRateSeconds;

    NotificationStrategyName notificationStrategyName;

    MessageStrategyName messageStrategyName;
}
