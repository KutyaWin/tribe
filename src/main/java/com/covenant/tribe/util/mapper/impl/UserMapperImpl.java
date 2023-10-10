package com.covenant.tribe.util.mapper.impl;

import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.domain.user.Profession;
import com.covenant.tribe.domain.user.Registrant;
import com.covenant.tribe.domain.user.RelationshipStatus;
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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Component
public class UserMapperImpl implements UserMapper {

    public User mapToUserFromUserGoogleRegistration(UserForSignInUpDTO userForSignInUpDTO, String googleUserId) {
        return User.builder()
                .googleId(googleUserId)
                .hasGoogleAuthentication(true)
                .firebaseId(userForSignInUpDTO.getFirebaseId())
                .username(userForSignInUpDTO.getUsername())
                .userEmail(userForSignInUpDTO.getEmail())
                .phoneNumber(userForSignInUpDTO.getPhoneNumber())
                .build();
    }

    @Override
    public User mapToUserFromUserVkRegistration(UserForSignInUpDTO userForSignInUpDTO, String vkUserId) {
        return User.builder()
                .vkId(vkUserId)
                .hasVkAuthentication(true)
                .firebaseId(userForSignInUpDTO.getFirebaseId())
                .username(userForSignInUpDTO.getUsername())
                .userEmail(userForSignInUpDTO.getEmail())
                .phoneNumber(userForSignInUpDTO.getPhoneNumber())
                .build();
    }

    public User buildUserFromConfirmRegistrationDTORegistrantAndUserInterests(
            ConfirmRegistrationDTO confirmRegistrationDTO, Set<EventType> userInterests, Registrant registrant
    ) {
        return User.builder()
                .hasEmailAuthentication(true)
                .firebaseId(confirmRegistrationDTO.getFirebaseId())
                .userEmail(registrant.getEmail())
                .password(registrant.getPassword())
                .phoneNumber(null)
                .username(registrant.getUsername())
                .enableGeolocation(false)
                .interestingEventType(userInterests)
                .build();
    }

    @Override
    public UserSubscriberDto mapToUserSubscriberDto(User subscriber, Set<Long> userIds, Map<Long, Long> userIdsWithChatsExist) {
        return UserSubscriberDto.builder()
                .username(subscriber.getUsername())
                .userId(subscriber.getId())
                .userAvatar(subscriber.getUserAvatar())
                .isUserSubscribeToSubscriber(userIds.contains(subscriber.getId()))
                .lastName(subscriber.getLastName())
                .firstName(subscriber.getFirstName())
                .chatId(userIdsWithChatsExist.get(subscriber.getId()))
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
    public UserGetDto mapToUserGetDto(
            User user,
            AuthMethodsDto authMethodsDto,
            List<ProfessionDto> professionDtoList,
            List<EventTypeInfoDto> eventTypeInfoDtoList
    ) {
        return UserGetDto.builder()
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

    @Override
    public ProfileDto mapToProfileDto(
            User user, boolean isFollowed, boolean isFollowing, Long chatId
    ) {
        return ProfileDto.builder()
                .userId(user.getId())
                .avatarUrl(user.getUserAvatar())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .age(user.getAge())
                .professions(
                        user.getUserProfessions().stream()
                                .map(Profession::getName)
                                .collect(Collectors.toList())
                )
                .followersCount(
                        (int) user.getFollowers().stream()
                                .filter(
                                        follower -> follower.getRelationshipStatus() == RelationshipStatus.SUBSCRIBE
                                )
                                .count()
                )
                .followingCount(
                        (int) user.getFollowing().stream()
                                .filter(
                                        follower -> follower.getRelationshipStatus() == RelationshipStatus.SUBSCRIBE
                                )
                                .count()
                )
                .isFollowed(isFollowed)
                .isFollowing(isFollowing)
                .interests(
                        user.getInterestingEventType().stream()
                                .map(EventType::getTypeName)
                                .collect(Collectors.toList())
                )
                .chatId(chatId)
                .build();
    }

    @Override
    public UserToSendInvitationDTO mapToUserToSendInvitationDTO(User user) {

        return UserToSendInvitationDTO.builder()
                .userId(user.getId())
                .userAvatar(user.getUserAvatar())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }
}
