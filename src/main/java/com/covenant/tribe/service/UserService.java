package com.covenant.tribe.service;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.user.UserToSendInvitationDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    User findUserByUsername(String username);

    List<User> findUserByPartialUsername(String partialUsername);

    List<UserToSendInvitationDTO> findUserByUsernameForSendInvite(String username);

    void saveEventToFavorite(Long userId, Long eventId);

    List<Event> getAllFavoritesByUserId(Long userId);

    void removeEventFromFavorite(Long userId, Long eventId);

    boolean isFavoriteEventForUser(Long userId, Long eventId);

    boolean isEmailExist(String email);

    boolean isUsernameExist(String username);
}
