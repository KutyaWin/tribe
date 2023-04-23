package com.covenant.tribe.service.impl;

import com.covenant.tribe.domain.UserRelationsWithEvent;
import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.user.TESTUserForSignUpDTO;
import com.covenant.tribe.dto.user.UserToSendInvitationDTO;
import com.covenant.tribe.exeption.event.EventNotFoundException;
import com.covenant.tribe.exeption.user.UserNotFoundException;
import com.covenant.tribe.exeption.user.UsernameDataAlreadyExistException;
import com.covenant.tribe.repository.EventRepository;
import com.covenant.tribe.repository.UserRepository;
import com.covenant.tribe.service.UserRelationsWithEventService;
import com.covenant.tribe.service.UserService;
import com.covenant.tribe.util.mapper.UserMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    UserMapper userMapper;
    UserRelationsWithEventService userRelationsWithEventService;
    EventRepository eventRepository;

    @Transactional
    public TESTUserForSignUpDTO saveTestNewUser(TESTUserForSignUpDTO user) {
        log.info("[TRANSACTION] Open transaction in class: " + this.getClass().getName());

        User userToSave = userMapper.mapToUser(user);
        userToSave = saveUser(userToSave);
        TESTUserForSignUpDTO userToReturn = userMapper.mapToTESTUserForSignUpDTO(userToSave);

        log.info("[TRANSACTION] Close transaction in class: " + this.getClass().getName());
        return userToReturn;
    }

    public User saveUser(User user) {
        if (isUsernameExist(user.getUsername())) {
            log.error("[EXCEPTION] User with passed username already exists. Username: {}", user.getUsername());
            throw new UsernameDataAlreadyExistException(
                    String.format("Passed username already exists: %s", user.getUsername()));
        }
        if (isEmailExist(user.getUserEmail())) {
            log.error("[EXCEPTION] User with passed email already exists. Email: {}", user.getUserEmail());
            throw new UsernameDataAlreadyExistException(
                    String.format("Passed email already exists: %s", user.getUserEmail()));
        }
        if (isPhoneNumberExist(user.getPhoneNumber())) {
            log.error("[EXCEPTION] User with passed phoneNumber already exists. PhoneNumber: {}", user.getPhoneNumber());
            throw new UsernameDataAlreadyExistException(
                    String.format("Passed phoneNumber already exists: %s", user.getPhoneNumber()));
        }

        user = userRepository.save(user);
        return user;
    }

    @Override
    public User findUserById(Long userId) {
        return userRepository.findUserById(userId).orElseThrow( () -> {
            log.error("[EXCEPTION] User with id: " + userId + " not found.");
            return new UserNotFoundException("User with id: " + userId + " not found.");
        });
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> {
                    log.error("[EXCEPTION] User with username: " + username + " not found.");
                    return new UserNotFoundException("User with username: " + username + " not found.");
                });
    }

    @Transactional
    @Override
    public void saveEventToFavorite(Long userId, Long eventId) {
        log.info("[TRANSACTION] Open transaction in class: " + this.getClass().getName());

        User user = findUserById(userId).getUserRelationsWithEvents().stream()
                .filter(userRelationsWithEvent -> userRelationsWithEvent.getEvent().getId().equals(eventId))
                .findFirst()
                .map(userRelationsWithEvent -> {
                    userRelationsWithEvent.setFavoriteEvent(true);
                    return userRelationsWithEvent.getUser();
                })
                .orElseGet(() -> {
                    UserRelationsWithEvent userRelationsWithEvent = UserRelationsWithEvent.builder()
                            .favoriteEvent(true).build();
                    userRelationsWithEvent.setEvent(eventRepository.findById(eventId)
                            .orElseThrow(() -> {
                                log.error("[EXCEPTION] event with id {}, does not exist", eventId);
                                return new EventNotFoundException(String.format("Event with id %s  does not exist", eventId));
                            }));
                    userRelationsWithEvent.setUser(findUserById(userId));
                    userRelationsWithEventService.saveUserRelationsWithEvent(userRelationsWithEvent);
                    return userRelationsWithEvent.getUser();
                });

        userRepository.save(user);

        log.info("[TRANSACTION] Close transaction in class: " + this.getClass().getName());
    }

    @Transactional(readOnly = true)
    @Override
    public UserToSendInvitationDTO findUserByUsernameForSendInvite(String username) {
        return userMapper.mapToUserToSendInvitationDTO(findUserByUsername(username));
    }

    public boolean isPhoneNumberExist(String phoneNumber) {
        return userRepository.existsUserByPhoneNumber(phoneNumber);
    }

    public boolean isEmailExist(String email) {
        return userRepository.existsUserByUserEmail(email);
    }

    public boolean isUsernameExist(String username) {
        return userRepository.existsUserByUsername(username);
    }

    @Override
    public boolean isFavoriteEventForUser(Long userId, Long eventId) {
        return findUserById(userId).getUserRelationsWithEvents().stream()
                .filter(userRelationsWithEvent -> userRelationsWithEvent.getEvent().getId().equals(eventId))
                .findFirst()
                .map(UserRelationsWithEvent::isFavoriteEvent)
                .orElse(false);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Event> getAllFavoritesByUserId(Long userId) {
        return findUserById(userId).getUserRelationsWithEvents().stream()
                .filter(UserRelationsWithEvent::isFavoriteEvent)
                .map(UserRelationsWithEvent::getEvent)
                .toList();
    }

    @Transactional
    @Override
    public void removeEventFromFavorite(Long userId, Long eventId) {
        log.info("[TRANSACTION] Open transaction in class: " + this.getClass().getName());

        User user = findUserById(userId).getUserRelationsWithEvents().stream()
                .filter(userRelationsWithEvent -> userRelationsWithEvent.getEvent().getId().equals(eventId))
                .findFirst()
                .map(userRelationsWithEvent -> {
                    userRelationsWithEvent.setFavoriteEvent(false);
                    return userRelationsWithEvent.getUser();
                })
                .orElseThrow(() -> {
                    log.error("[EXCEPTION] Event with id: " + eventId + " not found.");
                    return new EventNotFoundException("Event with id: " + eventId + " not found.");
                });

        userRepository.save(user);

        log.info("[TRANSACTION] Close transaction in class: " + this.getClass().getName());
    }
}
