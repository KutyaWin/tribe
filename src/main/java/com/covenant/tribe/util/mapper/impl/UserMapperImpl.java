package com.covenant.tribe.util.mapper.impl;

import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.user.TESTUserForSignUpDTO;
import com.covenant.tribe.dto.user.UserToSendInvitationDTO;
import com.covenant.tribe.util.mapper.UserMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Component
public class UserMapperImpl implements UserMapper {

    public User mapToUser(TESTUserForSignUpDTO userDto) {
        log.debug("map TESTUserForSignUpDTO to User. TESTUserForSignUpDTO: {}", userDto);

        return User.builder()
                .bluetoothId(userDto.getBluetoothId())
                .username(userDto.getUsername())
                .userEmail(userDto.getEmail())
                .password(userDto.getPassword())
                .phoneNumber(userDto.getPhoneNumber())
                .build();
    }

    public TESTUserForSignUpDTO mapToTESTUserForSignUpDTO(User user) {
        log.debug("map User to TESTUserForSignUpDTO. User: {}", user);

        return TESTUserForSignUpDTO.builder()
                .bluetoothId(user.getBluetoothId())
                .username(user.getUsername())
                .email(user.getUserEmail())
                .password(user.getPassword())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    @Override
    public UserToSendInvitationDTO mapToUserToSendInvitationDTO(User user) {
        log.debug("map User to UserToSendInvitationDTO. User: {}", user);

        return UserToSendInvitationDTO.builder()
                .userId(user.getId())
                .userAvatar(user.getUserAvatar())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }
}
