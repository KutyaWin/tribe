package com.covenant.tribe.service.impl;

import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.domain.user.Friendship;
import com.covenant.tribe.domain.user.Profession;
import com.covenant.tribe.domain.user.RelationshipStatus;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.ImageDto;
import com.covenant.tribe.dto.auth.AuthMethodsDto;
import com.covenant.tribe.dto.event.EventTypeInfoDto;
import com.covenant.tribe.dto.user.*;
import com.covenant.tribe.exeption.AlreadyExistArgumentForAddToEntityException;
import com.covenant.tribe.exeption.storage.FilesNotHandleException;
import com.covenant.tribe.exeption.user.SubscribeNotFoundException;
import com.covenant.tribe.exeption.user.UserAlreadyExistException;
import com.covenant.tribe.exeption.user.UserNotFoundException;
import com.covenant.tribe.repository.*;
import com.covenant.tribe.service.UserService;
import com.covenant.tribe.util.mapper.EventTypeMapper;
import com.covenant.tribe.util.mapper.ProfessionMapper;
import com.covenant.tribe.util.mapper.UserMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

    EventTypeRepository eventTypeRepository;
    FriendshipRepository friendshipRepository;
    UserRepository userRepository;
    UserMapper userMapper;
    ProfessionMapper professionMapper;
    EventTypeMapper eventTypeMapper;
    FileStorageRepository fileStorageRepository;
    ProfessionRepository professionRepository;


    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> {
                    log.error("[EXCEPTION] User with username: " + username + " not found.");
                    return new UserNotFoundException("User with username: " + username + " not found.");
                });
    }

    @Transactional(readOnly = true)
    @Override
    public Page<UserToSendInvitationDTO> findUsersByContainsStringInUsernameForSendInvite(String partUsername, Pageable pageable) {
        return userRepository.findAllByUsernameContains(partUsername, pageable)
                .map(userMapper::mapToUserToSendInvitationDTO);
    }

    public boolean isPhoneNumberExist(String phoneNumber) {
        return userRepository.existsUserByPhoneNumber(phoneNumber);
    }

    public boolean isEmailExist(String email) {
        return userRepository.existsUserByUserEmail(email);
    }

    public boolean isUsernameExist(String username) {
        return userRepository.existsUserByUsername(username);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<UserSubscriberDto> findAllSubscribersByUsername(String partialUsername, Long userId, Pageable pageable) {
        Page<User> subscribers = userRepository.findAllSubscribersByPartialUsername(
                userId, partialUsername, RelationshipStatus.SUBSCRIBE, pageable
        );
        List<Long> subscriberIds = subscribers.stream().map(User::getId).toList();
        Set<Long> subscribersToWhichUserIsSubscribed = userRepository.findMutuallySubscribed(subscriberIds, userId);

        return subscribers.map(user -> userMapper.mapToUserSubscriberDto(user, subscribersToWhichUserIsSubscribed));
    }

    ;

    @Transactional(readOnly = true)
    @Override
    public Page<UserSubscriberDto> findAllSubscribers(long userId, Pageable pageable) {
        Page<User> subscribers = userRepository.findAllSubscribers(userId, RelationshipStatus.SUBSCRIBE, pageable);
        List<Long> subscriberIds = subscribers.stream().map(User::getId).toList();
        Set<Long> subscribersToWhichUserIsSubscribed = userRepository.findMutuallySubscribed(subscriberIds, userId);
        return subscribers.map(user -> userMapper.mapToUserSubscriberDto(user, subscribersToWhichUserIsSubscribed));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<UserUnSubscriberDto> findAllUnSubscribers(long userId, Pageable pageable) {
        Page<User> unsubscribers = userRepository.findAllNotFollowingUser(userId, RelationshipStatus.SUBSCRIBE, pageable);
        return unsubscribers.map(user -> userMapper.mapToUserUnSubscriberDto(user));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<UserUnSubscriberDto> findAllUnSubscribersByUsername(
            String unsubscriberUsername, long userId, Pageable pageable
    ) {
        Page<User> unsubscribers = userRepository.findAllNotFollowingUserByPartialUsername(
                unsubscriberUsername, userId, RelationshipStatus.SUBSCRIBE, pageable
        );
        return unsubscribers.map(user -> userMapper.mapToUserUnSubscriberDto(user));
    }

    @Transactional(readOnly = true)
    @Override
    public UserProfileGetDto getUserProfile(long userId) {
        User user = findUserById(userId);
        AuthMethodsDto authMethodsDto = getAuthMethodsDto(user);
        List<ProfessionDto> professionDto = user.getUserProfessions().stream()
                .map(professionMapper::mapToProfessionDto)
                .toList();
        List<EventTypeInfoDto> eventTypeInfoDtoList = user.getInterestingEventType().stream()
                .map(eventTypeMapper::mapToEventTypeInfoDtoList)
                .toList();
        return userMapper.mapToUserProfileGetDto(user, authMethodsDto, professionDto, eventTypeInfoDtoList);
    }

    @Override
    public String uploadAvatarToTempFolder(long userId, ImageDto imageDto) {
        return fileStorageRepository.saveFileToTmpDir(imageDto.getContentType(), imageDto.getImage());
    }

    @Transactional
    @Override
    public void updateUserProfile(UserProfileUpdateDto userProfileUpdateDto) {
        User user = findUserById(userProfileUpdateDto.getUserId());
        if (!userProfileUpdateDto.getUserAvatar().isEmpty()) {
            if (user.getUserAvatar() == null || !userProfileUpdateDto.getUserAvatar().equals(user.getUserAvatar())) {
                try {
                    setNewUserAvatar(userProfileUpdateDto.getUserAvatar(), user);
                } catch (IOException ex) {
                    String message = String.format("[EXCEPTION] IOException with message: %s", ex.getMessage());
                    log.error(message);
                    throw new FilesNotHandleException(message);
                }
            }
        }

        if (user.getUsername() == null || !userProfileUpdateDto.getUsername().equals(user.getUsername())) {
            if (userRepository.existsUserByUsername(userProfileUpdateDto.getUsername())) {
                String message = String.format("[EXCEPTION] User with username: %s already exists",
                        userProfileUpdateDto.getUsername()
                );
                log.error(message);
                throw new UserAlreadyExistException(message);
            }
            user.setUsername(userProfileUpdateDto.getUsername());
        }

        if (user.getFirstName() == null || !userProfileUpdateDto.getFirstName().equals(user.getFirstName())) {
            user.setFirstName(userProfileUpdateDto.getFirstName());
        }

        if (user.getLastName() == null || !userProfileUpdateDto.getLastName().equals(user.getLastName())) {
            user.setLastName(userProfileUpdateDto.getLastName());
        }

        if (user.getBirthday() == null || !userProfileUpdateDto.getBirthday().isEqual(user.getBirthday())) {
            user.setBirthday(userProfileUpdateDto.getBirthday());
        }

        Set<EventType> newEventTypes = new HashSet<>(eventTypeRepository
                .findAllById(userProfileUpdateDto.getInterestingEventType()));
        user.addInterestingEventTypes(newEventTypes);

        if (!userProfileUpdateDto.getProfessionIds().isEmpty()) {
            List<String> afterHandlingProfessionNames = userProfileUpdateDto.getNewProfessions().stream()
                    .map(professionName -> {
                        String lowerCaseProfessionName = professionName.toLowerCase();
                        return lowerCaseProfessionName
                                .substring(0, 1)
                                .toUpperCase() +
                                lowerCaseProfessionName.substring(1);
                    })
                    .toList();
            Set<Profession> newProfessions = afterHandlingProfessionNames.stream()
                    .map(professionName -> {
                        return Profession.builder()
                                .name(professionName)
                                .build();
                    })
                    .collect(Collectors.toSet());
            professionRepository.saveAll(newProfessions);
            Set<Profession> professionsForUpdate = new HashSet<>(
                    professionRepository.findAllById(userProfileUpdateDto.getProfessionIds())
            );
            professionsForUpdate.addAll(newProfessions);
            user.addNewProfessions(professionsForUpdate);
        }

        if (userProfileUpdateDto.isGeolocationAvailable() != user.isEnableGeolocation()) {
            user.setEnableGeolocation(userProfileUpdateDto.isGeolocationAvailable());
        }

        userRepository.save(user);

        try {
            fileStorageRepository.deleteUnnecessaryAvatars(userProfileUpdateDto.getAvatarsFilenamesForDeleting());
        } catch (IOException e) {
            String message = String.format("[EXCEPTION] IOException with message: %s", e.getMessage());
            log.error(message);
            throw new FilesNotHandleException(message);
        }

    }

    private void setNewUserAvatar(String fileNameForAdding, User user) throws IOException {
        String newAvatarFileName = fileStorageRepository.addUserAvatar(fileNameForAdding);
        user.setUserAvatar(newAvatarFileName);
    }

    private AuthMethodsDto getAuthMethodsDto(User user) {
        return AuthMethodsDto.builder()
                .hasEmailAuthentication(user.hasEmailAuthentication())
                .hasGoogleAuthentication(user.hasGoogleAuthentication())
                .hasVkAuthentication(user.hasVkAuthentication())
                .hasWhatsAppAuthentication(user.hasWhatsappAuthentication())
                .hasTelegramAuthentication(user.hasTelegramAuthentication())
                .build();
    }

    @Transactional()
    @Override
    public void subscribeToUser(SubscriptionDto subscriptionDto) {
        User follower = findUserById(subscriptionDto.getFollowerUserId());
        User following = findUserById(subscriptionDto.getFollowingUserId());
        boolean isFriendshipExist = friendshipRepository
                .existsByUserWhoGetFollowerAndUserWhoMadeFollowingAndRelationshipStatus(
                        following, follower, RelationshipStatus.SUBSCRIBE
                );
        if (isFriendshipExist) {
            String message = String.format(
                    "User %s and %s are already friends", following.getUsername(), follower.getUsername()
            );
            log.error(message);
            throw new AlreadyExistArgumentForAddToEntityException(message);
        }
        Friendship friendship = Friendship.builder()
                .relationshipStatus(RelationshipStatus.SUBSCRIBE)
                .userWhoGetFollower(following)
                .userWhoMadeFollowing(follower)
                .build();
        friendshipRepository.save(friendship);
    }

    @Transactional
    @Override
    public void unsubscribeFromUser(SubscriptionDto subscriptionDto) {
        User follower = findUserById(subscriptionDto.getFollowerUserId());
        User following = findUserById(subscriptionDto.getFollowingUserId());
        Friendship friendship = friendshipRepository
                .findByUserWhoMadeFollowingAndUserWhoGetFollowerAndUnsubscribeAtIsNull(follower, following)
                .orElseThrow(() -> {
                    String message = String.format(
                            "User %s don't subscribe to user %s", following.getUsername(), follower.getUsername()
                    );
                    log.error(message);
                    return new SubscribeNotFoundException(message);
                });
        friendship.unsubscribeUser();
        friendshipRepository.save(friendship);
    }

    private User findUserById(Long userId) {
        return userRepository.findUserById(userId)
                .orElseThrow(() -> {
                    log.error("[EXCEPTION] User with id: " + userId + " not found.");
                    return new UserNotFoundException("User with id: " + userId + " not found.");
                });
    }
}
