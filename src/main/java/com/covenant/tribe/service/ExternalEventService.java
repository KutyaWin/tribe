package com.covenant.tribe.service;

import com.covenant.tribe.client.kudago.dto.KudagoEventDto;

import java.util.List;
import java.util.Map;

public interface ExternalEventService {
    List<KudagoEventDto> prepareEventsForCreating(
            Map<Long, KudagoEventDto> kudaGoEvents,
            int daysQuantityToFirstPublication
    );

}
