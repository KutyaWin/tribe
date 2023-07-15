package com.covenant.tribe.service.impl;

import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.repository.KudaGoEventRepository;
import com.covenant.tribe.service.ExternalEventService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExternalEventServiceImpl implements ExternalEventService {

    KudaGoEventRepository kudaGoRepository;

    @Override
    public List<KudagoEventDto> deleteExistingInDbEvents(
            Map<Long, KudagoEventDto> kudaGoEvents
    ) {
        List<Long> existingEventIds = kudaGoRepository.findAllRepeatableEventIds(
                kudaGoEvents.keySet(), List.of(LocalDate.now(), LocalDate.now().minusDays(1))
        );
        existingEventIds.forEach(kudaGoEvents::remove);
        return filterEventByPublicationDate(kudaGoEvents);
    }

    private List<KudagoEventDto> filterEventByPublicationDate(Map<Long, KudagoEventDto> events) {
        return events.values().stream()
                .filter(kudagoEvent -> {
                    return kudagoEvent
                            .getPublicationDate()
                            .isEqual(LocalDate.now().minusDays(1)) ||
                            kudagoEvent.getPublicationDate().isEqual(LocalDate.now());
                })
                .toList();
    }


}
