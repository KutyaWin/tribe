package com.covenant.tribe.service;

import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.ImageDto;
import com.covenant.tribe.dto.user.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface UserService {

    User findUserByIdFetchUserAsOrganizer(Long id);

    User findUserById(Long id);

    User findUserByUsername(String username);

    List<User> findAllById(Set<Long> usersId);

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

    List<User> findAllByInterestingEventTypeContaining(Long eventTypeId);
}
