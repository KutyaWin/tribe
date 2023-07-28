package com.covenant.tribe.service;


import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.dto.event.EventComparisonDto;
import com.covenant.tribe.dto.event.external.ExternalEventDescription;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CompareEventService {

    List<ExternalEventDescription> getExternalEventForAdding(
            List<EventComparisonDto> eventsFromDb, List<KudagoEventDto> eventsToDb
    );

}
