package com.covenant.tribe.service;

public interface ExternalEventHandlerFacade {
    void handleNewEvents(String sincePublicationDate);
}
