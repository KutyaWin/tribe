package com.covenant.tribe.facade;

public interface ExternalEventHandler {
    void handleNewEvents(Map<Long, Event...Dto>);
}
