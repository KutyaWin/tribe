package com.covenant.tribe.service;

import com.covenant.tribe.domain.event.EventAddress;
import org.springframework.stereotype.Service;

@Service
public interface EventAddressService {

    EventAddress saveNewEventAddress(EventAddress eventAddress);

}
