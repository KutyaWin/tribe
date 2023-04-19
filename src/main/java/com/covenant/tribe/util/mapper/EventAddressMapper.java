package com.covenant.tribe.util.mapper;

import com.covenant.tribe.domain.event.EventAddress;
import com.covenant.tribe.dto.event.EventAddressDTO;

public interface EventAddressMapper {

    EventAddressDTO mapToEventAddressDTO(EventAddress eventAddress);

    EventAddress mapToEventAddress(EventAddressDTO dto);
}
