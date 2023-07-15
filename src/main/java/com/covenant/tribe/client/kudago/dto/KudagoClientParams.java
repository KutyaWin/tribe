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
    Long actual_since;

    public KudagoClientParams(OffsetDateTime since) {
        actual_since = since.toEpochSecond();
    }
}
