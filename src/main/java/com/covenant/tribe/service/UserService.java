package com.covenant.tribe.service;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.user.SubscriptionDto;
import com.covenant.tribe.dto.user.UserSubscriberDto;
import com.covenant.tribe.dto.user.UserToSendInvitationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    User findUserByUsername(String username);

    Page<UserToSendInvitationDTO> findUsersByContainsStringInUsernameForSendInvite(String partUsername, Pageable pageable);

    void saveEventToFavorite(Long userId, Long eventId);

    List<Event> getAllFavoritesByUserId(Long userId);

    void removeEventFromFavorite(Long userId, Long eventId);

    boolean isFavoriteEventForUser(Long userId, Long eventId);

    boolean isEmailExist(String email);

    boolean isUsernameExist(String username);

    Page<UserSubscriberDto> findAllSubscribersByUsername(String partialUsername, Long userId, Pageable pageable);
    void subscribeToUser(SubscriptionDto subscriptionDto);

    void unsubscribeFromUser(SubscriptionDto subscriptionDto);

    Page<UserSubscriberDto> findAllSubscribers(long l, Pageable pageable);
}
