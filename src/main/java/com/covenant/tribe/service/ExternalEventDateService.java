package com.covenant.tribe.service;

import com.covenant.tribe.client.kudago.dto.KudagoDate;
import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.dto.event.external.ExternalEventDates;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public interface ExternalEventDateService {

    public Map<Long, ExternalEventDates> handleExternalEventDates(List<KudagoEventDto> kudaGoEvents);

    public LocalDateTime transformTimestampToLocalDateTime(KudagoDate kudagoDate, String timezone);

}
