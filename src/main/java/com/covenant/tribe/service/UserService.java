package com.covenant.tribe.service;

import org.springframework.stereotype.Service;

@Service
public interface UserService {
    void saveEventToFavorite(Long userId, Long eventId);
}
