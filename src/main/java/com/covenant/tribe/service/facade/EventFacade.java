package com.covenant.tribe.service.facade;

import com.covenant.tribe.dto.event.DetailedEventInSearchDTO;
import com.covenant.tribe.dto.event.RequestTemplateForCreatingEventDTO;

public interface EventFacade {

    DetailedEventInSearchDTO handleNewEvent(RequestTemplateForCreatingEventDTO requestTemplateForCreatingEventDTO);
}
