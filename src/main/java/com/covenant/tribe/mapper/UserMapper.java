package com.covenant.tribe.mapper;

import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.user.UserDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserMapper {

    public static User mapUserDTOtoUser(UserDTO userDTO) {
        log.debug("map UserDTO to User. UserDTO: {}", userDTO);
        return User.builder()
                .userEmail(userDTO.getEmail())
                .password(userDTO.getPassword())
                .phoneNumber(userDTO.getPhoneNumber())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .username(userDTO.getUsername())
                .birthday(userDTO.getBirthday())
                .userAvatar(userDTO.getUserAvatar())
                .bluetoothId(userDTO.getBluetoothId())
                .build();
    }

    public static UserDTO mapUserToUserDTO(User user) {
        log.debug("map User to UserDTO. Passed User: {}", user);
        return UserDTO.builder()
                .bluetoothId(user.getBluetoothId())
                .email(user.getUserEmail())
                .password(user.getPassword())
                .username(user.getUsername())
                .phoneNumber(user.getPhoneNumber())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthday(user.getBirthday())
                .userAvatar(user.getUserAvatar())
                .build();
    }
}
