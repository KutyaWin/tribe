package com.covenant.tribe.dto.event.external;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParsedAddressDto {

    String city;
    String street;
    String houseNumber;
    String building;
    String construction;

}
