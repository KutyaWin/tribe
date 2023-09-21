package com.covenant.tribe.service.impl;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventContactInfo;
import com.covenant.tribe.dto.event.EventContactInfoDto;
import com.covenant.tribe.exeption.NotFoundException;
import com.covenant.tribe.repository.EventContactInfoRepository;
import com.covenant.tribe.repository.EventRepository;
import com.covenant.tribe.service.EventContactInfoService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class EventContactInfoServiceImpl implements EventContactInfoService {

    EventContactInfoRepository eventContactInfoRepository;
    EventRepository eventRepository;

    @Override
    public void updateContactInfo(Set<EventContactInfoDto> contactInfoDtos, Long eventId) {
        Optional<Event> eventOp = eventRepository.findById(eventId);
        if (eventOp.isPresent()) {
            Event event = eventOp.get();
            if (contactInfoDtos == null || contactInfoDtos.isEmpty()) {
                event.setEventContactInfos(new HashSet<>());
                return;
            }
            Set<EventContactInfo> contactInfos = new HashSet<>();

            contactInfoDtos.forEach(contactInfoDto -> {
                EventContactInfo eventContactInfo = eventContactInfoRepository.findByContactAndContactType(
                        contactInfoDto.getContact(), contactInfoDto.getContactType()
                ).orElseGet(() -> {
                    EventContactInfo newEventContactInfo = EventContactInfo.builder()
                            .contact(contactInfoDto.getContact())
                            .contactType(contactInfoDto.getContactType())
                            .build();
                    eventContactInfoRepository.save(newEventContactInfo);
                    return newEventContactInfo;
                });
                contactInfos.add(eventContactInfo);
            });
            event.updateContactInfos(contactInfos);
        } else {
            String erMessage = "Event with id %s not found in method updateContactInfo"
                    .formatted(eventId);
            log.error(erMessage);
            throw new NotFoundException(erMessage);
        }
    }
}
