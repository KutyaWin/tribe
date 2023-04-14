package com.covenant.tribe.service;

import com.covenant.tribe.dto.event.EventDTO;
import org.springframework.stereotype.Service;

@Service
public interface EventService {
    public EventDTO getEventById(Long eventId, Long userId);
    public void addUserToEvent(Long eventId, Long userId);
}
