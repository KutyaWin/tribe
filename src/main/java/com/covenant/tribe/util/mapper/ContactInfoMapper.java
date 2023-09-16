package com.covenant.tribe.util.mapper;

import com.covenant.tribe.domain.event.EventContactInfo;
import com.covenant.tribe.dto.event.EventContactInfoDto;

import java.util.Set;

public interface ContactInfoMapper {

    Set<EventContactInfoDto> mapToEventContactInfoDtos(Set<EventContactInfo> eventContactInfos);

}
