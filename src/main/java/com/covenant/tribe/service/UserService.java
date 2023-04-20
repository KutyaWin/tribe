package com.covenant.tribe.service;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.event.EventInFavoriteDTO;
import com.covenant.tribe.dto.user.TESTUserForSignUpDTO;
import com.covenant.tribe.dto.user.UserToSendInvitationDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    TESTUserForSignUpDTO saveTestNewUser(TESTUserForSignUpDTO user);

    User saveUser(User user);

    User findUserById(Long userId);

    User findUserByUsername(String username);

    UserToSendInvitationDTO findUserByUsernameForSendInvite(String username);

    void saveEventToFavorite(Long userId, Long eventId);

    List<Event> getAllFavoritesByUserId(Long userId);

    void removeEventFromFavorite(Long userId, Long eventId);

    boolean isEmailExist(String email);

    boolean isUsernameExist(String username);
}
