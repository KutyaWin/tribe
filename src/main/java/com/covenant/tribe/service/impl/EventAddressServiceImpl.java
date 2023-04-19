package com.covenant.tribe.service.impl;

import com.covenant.tribe.domain.event.EventAddress;
import com.covenant.tribe.repository.EventAddressRepository;
import com.covenant.tribe.service.EventAddressService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventAddressServiceImpl implements EventAddressService {

    EventAddressRepository eventAddressRepository;

    @Override
    public EventAddress saveNewEventAddress(EventAddress eventAddress) {
        return eventAddressRepository.save(eventAddress);
    }

}
