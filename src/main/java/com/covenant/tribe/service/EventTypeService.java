package com.covenant.tribe.service;

import com.covenant.tribe.dto.event.EventTypeDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public interface EventTypeService {
    List<EventTypeDTO> getAllEventTypes();
}
