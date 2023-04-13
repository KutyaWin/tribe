package com.covenant.tribe.service;

import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.user.UserDTO;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    void saveEventToFavorite(Long userId, Long eventId);

    User saveUser(UserDTO userDTO);
}
