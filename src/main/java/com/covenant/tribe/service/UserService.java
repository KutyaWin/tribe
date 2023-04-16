package com.covenant.tribe.service;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.user.UserDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    void saveEventToFavorite(Long userId, Long eventId);

    List<Event> getAllFavoritesByUserId(Long userId);
    void removeEventFromFavorite(Long userId, Long eventId);

    User saveUser(UserDTO userDTO);
    boolean isEmailExist(String email);
    boolean isUsernameExist(String username);
}
