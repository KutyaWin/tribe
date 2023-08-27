package com.covenant.tribe.dto.event.external;

import com.covenant.tribe.dto.event.EventAddressDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class ExternalEventAddressDto {

    EventAddressDTO eventAddressDTO;
    Boolean isAddressExistInDb;

}
