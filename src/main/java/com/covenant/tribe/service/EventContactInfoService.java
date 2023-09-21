package com.covenant.tribe.service;

import com.covenant.tribe.dto.event.EventContactInfoDto;

import java.util.Set;

public interface EventContactInfoService {

    void updateContactInfo(Set<EventContactInfoDto> contactInfoDtos, Long eventId);

}
