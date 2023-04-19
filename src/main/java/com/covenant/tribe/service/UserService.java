package com.covenant.tribe.service;

import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.user.TESTUserForSignUpDTO;
import com.covenant.tribe.dto.user.UserToSendInvitationDTO;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    User saveUser(User user);

    User findUserById(Long userId);

    void saveEventToFavorite(Long userId, Long eventId);

    User findUserByUsername(String organizerUsername);

    UserToSendInvitationDTO findUserByUsernameForSendInvite(String username);

    TESTUserForSignUpDTO saveTestNewUser(TESTUserForSignUpDTO user);
}
