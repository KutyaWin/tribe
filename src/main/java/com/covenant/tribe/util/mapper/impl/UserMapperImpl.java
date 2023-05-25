package com.covenant.tribe.util.mapper.impl;

import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.domain.user.Registrant;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.auth.AuthMethodsDto;
import com.covenant.tribe.dto.auth.ConfirmRegistrationDTO;
import com.covenant.tribe.dto.event.EventTypeInfoDto;
import com.covenant.tribe.dto.user.*;
import com.covenant.tribe.util.mapper.UserMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Component
public class UserMapperImpl implements UserMapper {

    private final String VK_PREFIX = "vk";
    private final String GOOGLE_PREFIX = "google";
    private final String TRIBE_PREFIX = "tribe";

    public User mapToUserFromUserForSignInUpDTO(UserForSignInUpDTO userDto, String socialUserId) {
        log.debug("map UserForSignInUpDTO to User. UserForSignInUpDTO: {}", userDto);
        //Fake data используются до тех пор, пока не определимся с flow регистрации нового пользователя
        return User.builder()
                .socialId(socialUserId)
                .firebaseId(userDto.getFirebaseId())
                .bluetoothId(userDto.getBluetoothId())
                .username(userDto.getUsername())
                .userEmail(makeFakeDataIfNeededIsEmpty(userDto.getEmail(), socialUserId))
                .password(makeFakeDataIfNeededIsEmpty(userDto.getPassword(), socialUserId))
                .phoneNumber(makeFakeDataIfNeededIsEmpty(userDto.getPhoneNumber(), socialUserId))
                .build();
    }

    public User buildUserFromConfirmRegistrationDTORegistrantAndUserInterests(
            ConfirmRegistrationDTO confirmRegistrationDTO, Set<EventType> userInterests, Registrant registrant
    ) {
        log.debug("map confirmRegistrationDTO to User. confirmRegistrationDTO: {}, " +
                "registrant: {}, userInterests: {}", confirmRegistrationDTO, registrant, userInterests);
        String socialId = TRIBE_PREFIX + registrant.getId();
        return User.builder()
                .socialId(TRIBE_PREFIX + registrant.getId())
                .firebaseId(confirmRegistrationDTO.getFirebaseId())
                .userEmail(registrant.getEmail())
                .password(registrant.getPassword())
                .phoneNumber(socialId)
                .username(registrant.getUsername())
                .bluetoothId(confirmRegistrationDTO.getBluetoothId())
                .enableGeolocation(false)
                .interestingEventType(userInterests)
                .build();
    }

    @Override
    public UserSubscriberDto mapToUserSubscriberDto(User subscriber, Set<Long> userIds) {
        log.debug("map User to UserSubscriberDto. User: {}", subscriber);
        return UserSubscriberDto.builder()
                .userId(subscriber.getId())
                .userAvatar(subscriber.getUserAvatar())
                .isUserSubscribeToSubscriber(userIds.contains(subscriber.getId()))
                .lastName(subscriber.getLastName())
                .firstName(subscriber.getFirstName())
                .build();

    }

    @Override
    public UserUnSubscriberDto mapToUserUnSubscriberDto(User user) {
        return UserUnSubscriberDto.builder()
                .userAvatar(user.getUserAvatar())
                .userId(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    @Override
    public UserProfileGetDto mapToUserProfileGetDto(
            User user,
            AuthMethodsDto authMethodsDto,
            List<ProfessionDto> professionDtoList,
            List<EventTypeInfoDto> eventTypeInfoDtoList
    ) {
        return UserProfileGetDto.builder()
                .avatarUrl(user.getUserAvatar())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthday(user.getBirthday())
                .interestingEventType(eventTypeInfoDtoList)
                .professions(professionDtoList)
                .email(user.getUserEmail())
                .phoneNumber(user.getPhoneNumber())
                .availableAuthMethods(authMethodsDto)
                .isGeolocationAvailable(user.isEnableGeolocation())
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
