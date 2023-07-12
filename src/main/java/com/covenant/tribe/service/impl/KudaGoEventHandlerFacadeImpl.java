package com.covenant.tribe.service.impl;

import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.service.ExternalEventHandlerFacade;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class KudaGoEventHandlerFacadeImpl implements ExternalEventHandlerFacade {


    @Override
    public void handleNewEvents(Map<Long, KudagoEventDto> externalEvents) {

    }
}
