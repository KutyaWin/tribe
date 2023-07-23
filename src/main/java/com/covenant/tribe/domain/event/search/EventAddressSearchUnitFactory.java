package com.covenant.tribe.domain.event.search;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventAddress;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class EventAddressSearchUnitFactory {
    
    @Transactional
    public EventAddressSearchUnit create(Event event) {
        EventAddress eventAddress = event.getEventAddress();
        return EventAddressSearchUnit.builder()
                .eventLatitude(eventAddress.getEventLatitude())
                .eventLongitude(eventAddress.getEventLongitude())
                .names(getAddressNames(eventAddress))
                .eventPosition(eventAddress.getEventPosition())
                .build();
    }

    private ArrayList<String> getAddressNames(EventAddress eventAddress) {
        ArrayList<String> addressNames = new ArrayList<>();
        addressNames.add(eventAddress.getCity());
        addressNames.add(eventAddress.getStreet());
        addressNames.add(eventAddress.getDistrict());
        addressNames.add(eventAddress.getBuilding());
        addressNames.add(eventAddress.getHouseNumber());
        return addressNames;
    }
}