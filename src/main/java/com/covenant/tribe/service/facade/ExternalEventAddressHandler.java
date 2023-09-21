package com.covenant.tribe.service.facade;

import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.dto.event.EventAddressDTO;

public interface ExternalEventAddressHandler {

    EventAddressDTO handleExternalEventAddress(KudagoEventDto kudagoEventDto);

}
