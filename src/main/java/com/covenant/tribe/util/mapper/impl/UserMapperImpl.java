package com.covenant.tribe.util.mapper.impl;

import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.user.UserForSignInUpDTO;
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

    public User mapToUser(UserForSignInUpDTO userDto, String socialUserId) {
        log.debug("map UserForSignInUpDTO to User. UserForSignInUpDTO: {}", userDto);
        //Fake data используются до тех пор, пока не определимся с flow регистрации нового пользователя
        return User.builder()
                .socialId(socialUserId)
                .firebaseId(userDto.getFirebaseId())
                .bluetoothId(userDto.getBluetoothId())
                .username(makeFakeDataIfNeededIsEmpty("", socialUserId))
                .userEmail(makeFakeDataIfNeededIsEmpty(userDto.getEmail(), socialUserId))
                .password(makeFakeDataIfNeededIsEmpty(userDto.getPassword(), socialUserId))
                .phoneNumber(makeFakeDataIfNeededIsEmpty(userDto.getPhoneNumber(), socialUserId))
                .build();
    }

    private String makeFakeDataIfNeededIsEmpty(String data, String socialId) {
        if (data.isEmpty()) return socialId;
        return data;
    }

    public UserForSignInUpDTO mapToTESTUserForSignUpDTO(User user) {
        log.debug("map User to TESTUserForSignUpDTO. User: {}", user);

        return UserForSignInUpDTO.builder()
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
