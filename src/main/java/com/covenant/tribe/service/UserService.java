package com.covenant.tribe.service;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.ImageDto;
import com.covenant.tribe.dto.user.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    User findUserByUsername(String username);

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

    UserProfileGetDto getUserProfile(long userId);

    void uploadAvatarToTempFolder(long userId, ImageDto imageDto);

    void updateUserProfile(UserProfileUpdateDto userProfileUpdateDto);
}
