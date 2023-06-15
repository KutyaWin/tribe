package com.covenant.tribe.util.mapper;


import com.covenant.tribe.domain.Tag;
import com.covenant.tribe.dto.event.EventTagDTO;

import java.util.List;

public interface EventTagMapper {
         EventTagDTO mapEventTagToEventTagDTO(Tag eventTag);
         List<EventTagDTO> mapEventTagListToEventTagDtoList(List<Tag> eventTags);
}
