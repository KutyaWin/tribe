package com.covenant.tribe.service;

import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.domain.Tag;
import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.exeption.event.EventTypeNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public interface ExternalEventTagService {

    public Map<Long, List<Long>> handleNewExternalTags(List<KudagoEventDto> kudaGoEvents);

}
