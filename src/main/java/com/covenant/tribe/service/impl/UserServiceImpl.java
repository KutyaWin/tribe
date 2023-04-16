package com.covenant.tribe.service.impl;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.user.UserDTO;
import com.covenant.tribe.exeption.event.EventNotFoundException;
import com.covenant.tribe.exeption.user.UsernameDataAlreadyExistException;
import com.covenant.tribe.exeption.user.UserNotFoundException;
import com.covenant.tribe.mapper.UserMapper;
import com.covenant.tribe.repository.EventRepository;
import com.covenant.tribe.repository.UserRepository;
import com.covenant.tribe.service.UserService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    EventRepository eventRepository;

    @Transactional
    @Override
    public User saveUser(UserDTO userDTO) {
        log.info("[TRANSACTION] Open transaction in class: " + this.getClass().getName() + ", method: saveUser()");
        if (!userRepository.existsUserByUserEmail(userDTO.getEmail())) {
            if (!userRepository.existsUserByUsername(userDTO.getUsername())) {
                if (!userRepository.existsUserByPhoneNumber(userDTO.getPhoneNumber())) {
                    User newUser = UserMapper.mapUserDTOtoUser(userDTO);

                    log.debug("[REPOSITORY] Trying to save a new user: {}", newUser);
                    newUser = userRepository.save(newUser);

                    log.info("[TRANSACTION] Close transaction in class: " + this.getClass().getName() + ", method: saveUser()");
                    return newUser;
                } else {
                    log.error("[EXCEPTION] User with passed phoneNumber already exists. User phoneNumber: {}", userDTO.getPhoneNumber());
                    throw new UsernameDataAlreadyExistException(String.format("Passed phoneNumber already exists in DB: %s", userDTO.getPhoneNumber()));
                }
            } else {
                log.error("[EXCEPTION] User with passed username already exists. User username: {}", userDTO.getUsername());
                throw new UsernameDataAlreadyExistException(String.format("Passed username already exists in DB: %s", userDTO.getUsername()));
            }
        } else {
            log.error("[EXCEPTION] User with passed email already exists. User email: {}", userDTO.getEmail());
            throw new UsernameDataAlreadyExistException(String.format("Passed email already exists in DB: %s", userDTO.getEmail()));
        }
    }

    @Transactional
    @Override
    public void saveEventToFavorite(Long userId, Long eventId) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));
        Event event = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(
                        String.format(
                                "Event type with %s  does not exist",
                                eventId
                )));
        user.addFavoriteEvent(event);
    }

    @Transactional
    @Override
    public List<Event> getAllFavoritesByUserId(Long userId) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));
        return user.getFavoritesEvent();
    }

    @Transactional
    @Override
    public void removeEventFromFavorite(Long userId, Long eventId) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));
        Event event = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(
                        String.format(
                                "Event type with %s  does not exist",
                                eventId
                        )));
        user.removeFavoriteEvent(event);
    }
}
