package com.covenant.tribe.chat.service.impl;

import com.covenant.tribe.chat.controller.ws.WsChatController;
import com.covenant.tribe.chat.domain.Chat;
import com.covenant.tribe.chat.domain.Message;
import com.covenant.tribe.chat.dto.*;
import com.covenant.tribe.chat.factory.ChatFactory;
import com.covenant.tribe.chat.factory.MessageFactory;
import com.covenant.tribe.chat.repository.ChatRepository;
import com.covenant.tribe.chat.repository.MessageRepository;
import com.covenant.tribe.chat.service.ChatService;
import com.covenant.tribe.domain.event.EventAvatar;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.exeption.UnexpectedDataException;
import com.covenant.tribe.repository.EventAvatarRepository;
import com.covenant.tribe.repository.EventRepository;
import com.covenant.tribe.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatServiceImpl implements ChatService {

    ChatRepository chatRepository;
    MessageRepository messageRepository;
    EventAvatarRepository eventAvatarRepository;
    UserService userService;
    ChatFactory chatFactory;
    MessageFactory messageFactory;
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
        Chat chat = getChatById(chatId);
        if (!chat.getParticipant().contains(author)) {
            String erMessage = "User %s is not participant of chat with id %s".formatted(
                    author.getUsername(), chatId
            );
            log.error(erMessage);
            throw new UnexpectedDataException(erMessage);
        }
        Message message = messageFactory.makeMessage(content, author, chat);
        chat.addMessage(message);

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
                .createdAt(message.getCreatedAt())
                .build();
        List<Long> sendToIds = chat.getParticipant().stream()
                .map(User::getId)
                .toList();

        messageRepository.save(message);
        for (Long sendToId : sendToIds) {
            simpMessagingTemplate.convertAndSend(
                    replaceToAddress(sendToId.toString()),
                    chatMessageDto
            );
        }
    }

    @Override
    @Transactional
    public List<ChatDto> getChatsByUserId(Long userId) {
        User user = userService.findUserById(userId);
        Set<Chat> chats = user.getChats();
        return makeChatDtos(chats, user);
    }

    private List<ChatDto> makeChatDtos(Set<Chat> chats, User gettingChatParticipant) {
        List<ChatDto> chatDtos = new ArrayList<>();
        for (Chat chat : chats) {
            Message lastMessage = messageRepository
                    .findFirstByChatOrderByCreatedAtDesc(chat);
            String avatarUrl = null;
            String chatName = null;
            if (chat.getIsGroup()) {
                EventAvatar avatar = eventAvatarRepository.findFirstByEventId(chat.getEvent().getId());
                avatarUrl = avatar.getAvatarUrl();
                chatName = chat.getEvent().getEventName();
            } else {
                User secondParticipant = chat.getParticipant().stream()
                        .filter(participant -> !participant.equals(gettingChatParticipant))
                        .findFirst()
                        .orElseThrow(() -> {
                            String erMessage = "Chat with id %s don't contain second participant"
                                    .formatted(chat.getId());
                            log.error(erMessage);
                            return new UnexpectedDataException(erMessage);
                        });
                avatarUrl = secondParticipant.getUserAvatar();
                chatName = secondParticipant.getUsername();
            }
            chatDtos.add(new ChatDto(
                            lastMessage == null ? null : lastMessage.getText(),
                            lastMessage == null ? null : lastMessage.getCreatedAt(),
                            null,
                            avatarUrl,
                            chat.getIsGroup(),
                            chatName
                    )
            );
        }
        return chatDtos;
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
