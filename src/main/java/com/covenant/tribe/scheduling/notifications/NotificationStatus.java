package com.covenant.tribe.scheduling.notifications;

import lombok.Getter;

@Getter
public enum NotificationStatus {
    NEW,
    SUCCESSFULLY_SENT,
    FAILED_TO_SENT,
    ERROR;

}
