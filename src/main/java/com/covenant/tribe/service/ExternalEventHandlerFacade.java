package com.covenant.tribe.service;

import com.covenant.tribe.client.kudago.dto.KudagoEventDto;

import java.util.Map;

public interface ExternalEventHandlerFacade {
    void handleNewEvents(int daysQuantityToFirstPublication);
}
