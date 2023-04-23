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

    private final String VK_PREFIX = "vk";
    private final String GOOGLE_PREFIX = "google";

    public User mapToUser(TESTUserForSignUpDTO userDto, String socialTypeFromHeader) {
        log.debug("map TESTUserForSignUpDTO to User. TESTUserForSignUpDTO: {}", userDto);
        String socialId = "";
        if (socialTypeFromHeader.equals(VK_PREFIX)) socialId = VK_PREFIX + userDto.getUserId();
        if (socialTypeFromHeader.equals(GOOGLE_PREFIX)) socialId = GOOGLE_PREFIX + userDto.getUserId();
        //Fake data используются до тех пор, пока не определимся с flow регистрации нового пользователя
        return User.builder()
                .socialId(socialId)
                .firebaseId(userDto.getFirebaseId())
                .bluetoothId(userDto.getBluetoothId())
                .username(makeFakeDataIfNeededIsEmpty("", socialId))
                .userEmail(makeFakeDataIfNeededIsEmpty(userDto.getEmail(), socialId))
                .password(makeFakeDataIfNeededIsEmpty(userDto.getPassword(), socialId))
                .phoneNumber(makeFakeDataIfNeededIsEmpty(userDto.getPhoneNumber(), socialId))
                .build();
    }

    private String makeFakeDataIfNeededIsEmpty(String data, String socialId) {
        if (data.isEmpty()) return socialId;
        return data;
    }

    public TESTUserForSignUpDTO mapToTESTUserForSignUpDTO(User user) {
        log.debug("map User to TESTUserForSignUpDTO. User: {}", user);

        return TESTUserForSignUpDTO.builder()
                .bluetoothId(user.getBluetoothId())
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
