package com.covenant.tribe.service;

import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.domain.event.EventContactInfo;

import java.util.List;
import java.util.Map;

public interface ExternalEventContactService {

    Map<Long, List<EventContactInfo>> handleEventContactsInfo(List<KudagoEventDto> kudagoEventDtos);

}
