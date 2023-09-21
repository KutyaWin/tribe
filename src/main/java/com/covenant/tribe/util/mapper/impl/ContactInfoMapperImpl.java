package com.covenant.tribe.util.mapper.impl;

import com.covenant.tribe.domain.event.EventContactInfo;
import com.covenant.tribe.dto.event.EventContactInfoDto;
import com.covenant.tribe.util.mapper.ContactInfoMapper;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ContactInfoMapperImpl implements ContactInfoMapper {
    @Override
    public Set<EventContactInfoDto> mapToEventContactInfoDtos(Set<EventContactInfo> eventContactInfos) {
        return eventContactInfos.stream()
                .map(this::mapToEventContactInfoDto)
                .collect(Collectors.toSet());
    }

    private EventContactInfoDto mapToEventContactInfoDto(EventContactInfo eventContactInfo) {
        return EventContactInfoDto.builder()
                .contact(eventContactInfo.getContact())
                .contactType(eventContactInfo.getContactType())
                .build();
    }
}
