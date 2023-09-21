package com.covenant.tribe.service;

import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface ExternalEventTagService {

    public Map<Long, List<Long>> handleNewExternalTags(List<KudagoEventDto> kudaGoEvents);

}
