package com.covenant.tribe.client.kudago.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KudagoCoordsDto {
    Double lat;
    Double lon;
}