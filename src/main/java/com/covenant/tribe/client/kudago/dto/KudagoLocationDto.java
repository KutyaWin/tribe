package com.covenant.tribe.client.kudago.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KudagoLocationDto {
    public String slug;
    public String name;
    public String timezone;
    public KudagoCoordsDto coords;
    public String language;
    public String currency;

}
