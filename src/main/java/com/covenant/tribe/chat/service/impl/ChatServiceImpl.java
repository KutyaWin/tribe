package com.covenant.tribe.chat.service.impl;

import com.covenant.tribe.chat.controller.ws.WsChatController;
import com.covenant.tribe.chat.domain.Chat;
import com.covenant.tribe.chat.dto.AuthorDto;
import com.covenant.tribe.chat.dto.ChatMessageDto;
import com.covenant.tribe.chat.dto.PrivateChatInfoDto;
import com.covenant.tribe.chat.dto.PrivateChatInvitedUserDto;
import com.covenant.tribe.chat.factory.ChatFactory;
import com.covenant.tribe.chat.repository.ChatRepository;
import com.covenant.tribe.chat.service.ChatService;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.exeption.UnexpectedDataException;
import com.covenant.tribe.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatServiceImpl implements ChatService {

    ChatRepository chatRepository;
    UserService userService;
    ChatFactory chatFactory;
    SimpMessagingTemplate simpMessagingTemplate;
    @Transactional
    @Override
    public PrivateChatInfoDto createPrivateChat(PrivateChatInvitedUserDto invitedUserDto, Long chatCreatorId) {

        log.debug("[TRANSACTIONAL] Open transaction in class" + ChatServiceImpl.class.getName());

        User invitedUser = userService.findUserById(invitedUserDto.invitedUserId());
        User chatCreator = userService.findUserById(chatCreatorId);
        Set<User> participants = Set.of(invitedUser, chatCreator);
        List<Long> chatIds = chatRepository.findAllByParticipantInAndIsGroup(participants, false);
        if (chatIds.size() > 1) {
            String erMessage = "There are more than one chat with users: %s and %s".formatted(
                    invitedUser.getUsername(), chatCreator.getUsername()
            );
            log.error(erMessage);
            throw new UnexpectedDataException(erMessage);
        }
        if (!chatIds.isEmpty()) {
            String erMessage = "Private chat with users: %s and %s already exists".formatted(
                    invitedUser.getUsername(), chatCreator.getUsername()
            );
            log.error(erMessage);
            throw new UnexpectedDataException(erMessage);
        }
        Chat newChat = chatFactory.makeChat(Set.of(invitedUser, chatCreator), false);
        chatRepository.save(newChat);

        log.debug("[TRANSACTIONAL] Close transaction in class" + ChatServiceImpl.class.getName());

        return new PrivateChatInfoDto(newChat.getId());
    }

    @Override
    @Transactional
    public void sendMessageToSubscribers(Long authorId, Long chatId, String content) {
        User author = userService.findUserById(authorId);
        AuthorDto authorDto = AuthorDto.builder()
                .authorId(authorId)
                .avatarUrl(author.getUserAvatar())
                .username(author.getUsername())
                .firstname(author.getFirstName())
                .lastname(author.getLastName())
                .build();
        ChatMessageDto chatMessageDto = ChatMessageDto.builder()
                .chatId(chatId)
                .author(authorDto)
                .content(content)
                .build();
        Chat chat = getChatById(chatId);
        List<Long> sendToIds = chat.getParticipant().stream()
                .map(User::getId)
                .toList();
        for (Long sendToId : sendToIds) {
            simpMessagingTemplate.convertAndSend(
                    replaceToAddress(sendToId.toString()),
                    chatMessageDto
            );
        }
    }

    private Chat getChatById(Long chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> {
                   String erMessage = "Chat with id %s not found".formatted(chatId);
                   log.error(erMessage);
                   return new EntityNotFoundException(erMessage);
                });
    }

    private String replaceToAddress(String addr) {
        return WsChatController.SUBSCRIBE_TO_MESSAGES.replace("{user_id}", addr);
    }

}
