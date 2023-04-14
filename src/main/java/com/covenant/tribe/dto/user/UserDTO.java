package com.covenant.tribe.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO {

    @JsonProperty("bluetooth_id")
    @Size(max = 100, message = "bluetooth_id must not consist of more than 100 characters")
    String bluetoothId;

    @Size(max = 50, message = "Email must not consist of more than 50 characters")
    @NotBlank(message = "Email should not be null or empty")
    @Email(message = "Enter the correct email")
    String email;

    @NotNull(message = "Password should not be null")
    @Size(max = 500, message = "Password must not consist of more than 500 characters")
    String password;

    @Size(max = 100, message = "username must not consist of more than 100 characters")
    @NotBlank(message = "usernam should not be null or empty or empty")
    String username;

    @JsonProperty("phone_number")
    @Pattern(regexp = "^\\+?[1-9][0-9]{7,17}$",
            message = "Phone number can start with the symbol +, must have from 7 to 14 numbers")
    String phoneNumber;

    @JsonProperty("first_name")
    @Size(max = 100, message = "first_name must not consist of more than 100 characters")
    String firstName;

    @JsonProperty("last_name")
    @Size(max = 100, message = "last_name must not consist of more than 100 characters")
    String lastName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "birthday date should be past")
    LocalDate birthday;

    @JsonProperty("user_avatar")
    @Size(max = 100, message = "user_avatar must not consist of more than 100 characters")
    String userAvatar;
}
