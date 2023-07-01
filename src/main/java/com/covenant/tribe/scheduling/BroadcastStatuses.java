package com.covenant.tribe.scheduling;

import lombok.Getter;

@Getter
public enum BroadcastStatuses {
    NEW("new"),
    IN_PROGRESS("in_progress"),
    COMPLETE_SUCCESSFULLY("complete_successfully"),
    PARTIALLY_COMPLETED("partially_completed"),
    ENDED_WITH_ERROR("ended_with_error"),
    FAILED_TO_COMPLETE("filed_to_complete")
    ;
    private String text;

    BroadcastStatuses(String s) {
        this.text = s;
    }
}
