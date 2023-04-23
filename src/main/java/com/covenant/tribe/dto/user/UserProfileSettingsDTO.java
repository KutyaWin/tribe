package com.covenant.tribe.dto.user;

import com.covenant.tribe.dto.event.EventTypeDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileSettingsDTO implements Serializable {

    @JsonProperty("bluetooth_id")
    @NotBlank(message = "bluetooth_id should not be null or empty or empty")
    @Size(max = 100, message = "bluetooth_id must not consist of more than 100 characters")
    String bluetoothId;

    @JsonProperty("user_avatar")
    @Size(max = 200, message = "user_avatar must not consist of more than 200 characters")
    String userAvatar;

    @Size(max = 100, message = "username must not consist of more than 100 characters")
    @NotBlank(message = "username should not be null or empty or empty")
    String username;

    @JsonProperty("first_name")
    @Size(max = 100, message = "first_name must not consist of more than 100 characters")
    String firstName;

    @JsonProperty("last_name")
    @Size(max = 100, message = "last_name must not consist of more than 100 characters")
    String lastName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "birthday date should be past")
    LocalDate birthday;

    @JsonProperty("interesting_event_type")
    @UniqueElements(message = "All elements in interesting_event_type must be unique")
    Set<EventTypeDTO> interestingEventType = new HashSet<>();

    //todo: нужна ли профессия?

    @Size(max = 50, message = "Email must not consist of more than 50 characters")
    @NotBlank(message = "Email should not be null or empty")
    @Email(message = "Enter the correct email")
    String email;

    @NotNull(message = "Password should not be null")
    @Size(max = 500, message = "Password must not consist of more than 500 characters")
    String password;

    @JsonProperty("phone_number")
    @Pattern(regexp = "^\\+?[1-9][0-9]{7,17}$",
            message = "Phone number can start with the symbol +, must have from 7 to 14 numbers")
    String phoneNumber;

    @JsonProperty("enable_geolocation")
    @NotNull(message = "enable_geolocation should not be null.")
    Boolean enableGeolocation;
}
