package com.covenant.tribe.chat.service.impl;

import com.covenant.tribe.chat.domain.Chat;
import com.covenant.tribe.chat.dto.PrivateChatInfoDto;
import com.covenant.tribe.chat.dto.PrivateChatInvitedUserDto;
import com.covenant.tribe.chat.factory.ChatFactory;
import com.covenant.tribe.chat.repository.ChatRepository;
import com.covenant.tribe.chat.service.PrivateChatService;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.exeption.UnexpectedDataException;
import com.covenant.tribe.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PrivateChatServiceImpl implements PrivateChatService {

    ChatRepository chatRepository;
    UserService userService;
    ChatFactory chatFactory;
    @Transactional
    @Override
    public PrivateChatInfoDto createPrivateChat(PrivateChatInvitedUserDto invitedUserDto, Long chatCreatorId) {

        log.debug("[TRANSACTIONAL] Open transaction in class" + PrivateChatServiceImpl.class.getName());

        User invitedUser = userService.findUserById(invitedUserDto.invitedUserId());
        User chatCreator = userService.findUserById(chatCreatorId);
        Set<User> participants = Set.of(invitedUser, chatCreator);
        List<Chat> chats = chatRepository.findAllByParticipantInAndIsGroup(participants, false);
        if (!chats.isEmpty()) {
            String erMessage = "Private chat with users: %s and %s already exists".formatted(
                    invitedUser.getUsername(), chatCreator.getUsername()
            );
            log.error(erMessage);
            throw new UnexpectedDataException(erMessage);
        }
        Chat newChat = chatFactory.makeChat(Set.of(invitedUser, chatCreator), false);
        chatRepository.save(newChat);

        log.debug("[TRANSACTIONAL] Close transaction in class" + PrivateChatServiceImpl.class.getName());

        return new PrivateChatInfoDto(newChat.getId());
    }
}
