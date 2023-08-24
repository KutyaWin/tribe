package com.covenant.tribe.dto.event.external;

import com.covenant.tribe.dto.event.EventAddressDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExternalEventAddressDto {

    EventAddressDTO eventAddressDTO;
    Boolean isAddressExistInDb;

}
