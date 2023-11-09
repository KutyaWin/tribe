package com.covenant.tribe.service;

import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.ImageDto;
import com.covenant.tribe.dto.user.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    User findUserByIdFetchUserAsOrganizer(Long id);

    User findUserById(Long id);

    User findUserByUsername(String username);

    List<User> findAllById(List<Long> usersId);

    Page<UserToSendInvitationDTO> findUsersByContainsStringInUsernameForSendInvite(String partUsername, Pageable pageable);
    boolean isEmailExist(String email);

    boolean isUsernameExist(String username);

    Page<UserSubscriberDto> findAllSubscribersByUsername(String partialUsername, Long userId, Pageable pageable);
    void subscribeToUser(SubscriptionDto subscriptionDto);

    void unsubscribeFromUser(SubscriptionDto subscriptionDto);

    Page<UserSubscriberDto> findAllSubscribers(long l, Pageable pageable);

    Page<UserUnSubscriberDto> findAllUnSubscribers(long userId, Pageable pageable);

    Page<UserUnSubscriberDto> findAllUnSubscribersByUsername(
            String unsubscriberUsername, long userId, Pageable pageable
    );

    UserGetDto getUser(long userId);

    String uploadAvatarToTempFolder(long userId, ImageDto imageDto);

    void updateUserProfile(UserProfileUpdateDto userProfileUpdateDto);

    ProfileDto getProfile(long profileOwnerId, long userWhoRequestedId);

    void sendConfirmationCodeToEmail(UserEmailDto userEmailDto);

    void confirmEmailChange(EmailChangeDto emailConfirmCodeDto);

    List<User> findAllByInterestingEventTypeContaining(Long eventTypeId);

    List<User> findAllSubscribedToEvent(Long eventId);

    void deleteUser(long userId);

    List<User> findAll();

    Page<UserSubscriberDto> findAllFollowing(long userId, Pageable pageable);
    Page<UserSubscriberDto> findAllFollowingsByUsername(String username, Long userId, Pageable pageable);

    boolean isPhoneNumberExist(String phoneNumber);
}
