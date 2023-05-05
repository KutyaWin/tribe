package com.covenant.tribe.util.mapper;


import com.covenant.tribe.domain.Tag;
import com.covenant.tribe.dto.event.EventTagDTO;

public interface EventTagMapper {
         EventTagDTO mapEventTagToEventTagDTO(Tag eventTag);
}
