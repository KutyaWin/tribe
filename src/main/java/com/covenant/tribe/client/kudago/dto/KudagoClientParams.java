package com.covenant.tribe.client.kudago.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KudagoClientParams {
    Long actualPublicationDate;

    public KudagoClientParams(OffsetDateTime since) {
        actualPublicationDate = since.toEpochSecond();
    }
}
