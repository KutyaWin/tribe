package com.covenant.tribe.service.impl;

import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.domain.event.ContactType;
import com.covenant.tribe.domain.event.EventContactInfo;
import com.covenant.tribe.exeption.NotFoundException;
import com.covenant.tribe.repository.EventContactInfoRepository;
import com.covenant.tribe.service.ExternalEventContactService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExternalEventContactServiceImpl implements ExternalEventContactService {

    EventContactInfoRepository eventContactInfoRepository;

    @Override
    public Map<Long, List<EventContactInfo>> handleEventContactsInfo(List<KudagoEventDto> kudagoEventDtos) {
        Map<Long, List<EventContactInfo>> eventContactInfos = new HashMap<>();
        ContactType kudagoDefaultContactType = ContactType.URL;
        String kudagoDefaultContact = "https://kudago.com/";
        EventContactInfo kudagoDefaultEventContactInfo = eventContactInfoRepository.findByContactAndContactType(
                kudagoDefaultContact, kudagoDefaultContactType
        ).orElseThrow(() -> {
            String erMessage = "There is no default kudago contact info in db";
            log.error(erMessage);
            return new NotFoundException(erMessage);
        });
        kudagoEventDtos.forEach(kudagoEventDto -> {
            if (kudagoEventDto.getSiteUrl() != null && !kudagoEventDto.getSiteUrl().isBlank()) {
                EventContactInfo eventContactInfo = eventContactInfoRepository.findByContactAndContactType(
                        kudagoEventDto.getSiteUrl(), kudagoDefaultContactType
                ).orElseGet(() -> {
                   EventContactInfo newEventContactInfo = EventContactInfo.builder()
                           .contact(kudagoEventDto.getSiteUrl())
                           .contactType(kudagoDefaultContactType)
                           .build();
                   eventContactInfoRepository.save(newEventContactInfo);
                   return newEventContactInfo;
                });
                eventContactInfos.put(kudagoEventDto.getId(), List.of(eventContactInfo));
            } else {
                eventContactInfos.put(kudagoEventDto.getId(), List.of(kudagoDefaultEventContactInfo));
            }
        });
        return eventContactInfos;
    }
}
