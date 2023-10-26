package com.covenant.tribe.chat.service.impl;

import com.covenant.tribe.chat.controller.ws.WsChatController;
import com.covenant.tribe.chat.dto.UserConnectionDto;
import com.covenant.tribe.chat.service.UserConnectionService;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.exeption.UnexpectedDataException;
import com.covenant.tribe.repository.UserRepository;
import com.covenant.tribe.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserConnectionServiceImpl implements UserConnectionService {

    public static boolean USER_CONNECTED = true;
    public static boolean USER_DISCONNECTED = false;

    UserService userService;
    UserRepository userRepository;
    SimpMessagingTemplate simpMessagingTemplate;
    @Transactional
    @Override
    public void userConnected(Principal principal) {
        if (principal == null) {
            String erMessage = "Principal cannot be null";
            log.error(erMessage);
            throw new UnexpectedDataException(erMessage);
        }
        Long userId = getUserIdFromPrincipal(principal);
        log.info("User with id {} connected to chats", userId);

        User participant = userService.findUserById(userId);
        participant.setOnline(USER_CONNECTED);
        userRepository.save(participant);

        UserConnectionDto userConnectionDto = new UserConnectionDto(
                userId, USER_CONNECTED
        );
        String destination = replaceToAddress(userId);
        simpMessagingTemplate.convertAndSend(
                destination,
                userConnectionDto
        );
    }

    @Transactional
    @Override
    public void userDisconnected(Principal principal) {
        if (principal == null) {
            String erMessage = "Principal cannont be null";
            log.error(erMessage);
            throw new UnexpectedDataException(erMessage);
        }
        Long userId = getUserIdFromPrincipal(principal);
        log.info("User with id {} disconnected to chats", userId);
        User participant = userService.findUserById(userId);
        participant.setOnline(USER_DISCONNECTED);
        userRepository.save(participant);
        UserConnectionDto userConnectionDto = new UserConnectionDto(
                userId, USER_DISCONNECTED
        );
        String destination = replaceToAddress(userId);
        simpMessagingTemplate.convertAndSend(
                destination,
                userConnectionDto
        );
    }

    private Long getUserIdFromPrincipal(Principal user) {
        return Long.valueOf(user.getName());
    }

    private String replaceToAddress(Long userId) {
        return WsChatController
                .SUBSCRIBE_TO_USER_CONNECTION
                .replaceFirst("\\{.+}", userId.toString());
    }
}
