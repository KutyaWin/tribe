package com.covenant.tribe.service.impl;

import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.repository.KudaGoEventRepository;
import com.covenant.tribe.service.ExternalEventService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExternalEventServiceImpl implements ExternalEventService {

    KudaGoEventRepository KudaGoRepository;

    @Override
    public List<KudagoEventDto> deleteExistingEvents(Map<Long, KudagoEventDto> kudaGoEvents) {
        List<Long> existingEventIds = kudaGoRepository.fin
    }



}
