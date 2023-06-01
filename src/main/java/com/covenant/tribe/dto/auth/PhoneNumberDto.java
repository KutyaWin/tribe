package com.covenant.tribe.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PhoneNumberDto implements Serializable {

    @NotBlank(message = "phoneNumber should not be null or empty")
    @Schema(example = "+79282223344")
    String phoneNumber;

}
