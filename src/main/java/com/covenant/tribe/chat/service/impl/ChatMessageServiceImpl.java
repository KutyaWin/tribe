package com.covenant.tribe.chat.service.impl;

import com.covenant.tribe.chat.domain.Chat;
import com.covenant.tribe.chat.domain.LastReadMessage;
import com.covenant.tribe.chat.domain.Message;
import com.covenant.tribe.chat.dto.AuthorDto;
import com.covenant.tribe.chat.dto.ChatMessageDto;
import com.covenant.tribe.chat.dto.UnreadMessageCountDto;
import com.covenant.tribe.chat.repository.ChatRepository;
import com.covenant.tribe.chat.repository.LastReadMessageRepository;
import com.covenant.tribe.chat.repository.MessageRepository;
import com.covenant.tribe.chat.service.ChatMessageService;
import com.covenant.tribe.chat.service.MessageReadService;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.exeption.UnexpectedDataException;
import com.covenant.tribe.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    MessageRepository messageRepository;
    LastReadMessageRepository lastReadMessageRepository;
    ChatRepository chatRepository;
    UserService userService;
    MessageReadService messageReadService;

    @Transactional
    @Override
    public Page<ChatMessageDto> getMessagesByChatId(Long userId, Long chatId, Pageable pageable) {
        User user = userService.findUserById(userId);
        Optional<LastReadMessage> lastReadMessage = lastReadMessageRepository.
                findByChatIdAndParticipantId(chatId, userId);
        long lastReadMessageId;
        if (lastReadMessage.isPresent()) {
            lastReadMessageId = lastReadMessage.get().getMessage().getId();
        } else {
            lastReadMessageId = 0;
        }
        checkUserParticipationInChat(user, chatId);
        Page<Message> messages = messageRepository.findAllByChatIdOrderByCreatedAtDesc(chatId, pageable);
        return messages.map(message -> {
            User author = message.getAuthor();
            AuthorDto authorDto = AuthorDto.builder()
                    .authorId(author.getId())
                    .lastname(author.getLastName())
                    .firstname(author.getFirstName())
                    .username(author.getUsername())
                    .avatarUrl(author.getUserAvatar())
                    .build();
            return new ChatMessageDto(
                    message.getChat().getId(),
                    message.getId(),
                    isMessageReadCheck(message, lastReadMessageId),
                    authorDto,
                    message.getText(),
                    message.getCreatedAt()
            );
        });
    }

    private boolean isMessageReadCheck(Message message, long lastReadMessageId) {
        return lastReadMessageId != 0 && message.getId() <= lastReadMessageId;
    }

    @Transactional
    @Override
    public void setLastReadMessage(Long userId, Long chatId, Long messageId) {
        User user = userService.findUserById(userId);
        checkUserParticipationInChat(user, chatId);
        Optional<LastReadMessage> lastReadMessageOptional = lastReadMessageRepository
                .findByChatIdAndParticipantId(chatId, userId);
        Optional<Message> newLastMessage = messageRepository.findById(messageId);
        Optional<Chat> chat = chatRepository.findById(chatId);
        if (chat.isEmpty()) {
            String erMessage = "Chat with id %s can not be null".formatted(chatId);
            log.error(erMessage);
            throw new UnexpectedDataException(erMessage);
        }
        if (newLastMessage.isEmpty()) {
            String erMessage = "Message with id %s can not be null".formatted(messageId);
            log.error(erMessage);
            throw new UnexpectedDataException(erMessage);
        }
        LastReadMessage lastReadMessage;
        if (lastReadMessageOptional.isPresent()) {
            lastReadMessage = lastReadMessageOptional.get();
            checkMessageId(lastReadMessage.getMessage().getId(), messageId);

            lastReadMessage.setMessage(newLastMessage.get());
        } else {
            lastReadMessage = LastReadMessage.builder()
                    .chat(chat.get())
                    .participant(user)
                    .message(newLastMessage.get())
                    .build();
        }
        lastReadMessageRepository.save(lastReadMessage);
        messageReadService.sendMessageToSubscribers(user, chat.get(), lastReadMessage);
    }

    @Transactional(readOnly = true)
    @Override
    public int countUnreadMessagesByChatAndUser(User participant, Chat chat) {
        Message lastReadMessage = null;
        Optional<LastReadMessage> lastReadMessageOptional = lastReadMessageRepository.findByChatIdAndParticipantId(
                chat.getId(), participant.getId()
        );
        int unreadMessageCount;
        if (lastReadMessageOptional.isPresent()) {
            lastReadMessage = lastReadMessageOptional.get().getMessage();
            unreadMessageCount = messageRepository.countMessageByAuthorNotAndChatAndIdAfter(
                    participant, chat, lastReadMessage.getId()
            );
        } else {
            unreadMessageCount = messageRepository.countMessageByAuthorNotAndChat(
                    participant, chat
            );
        }
        return unreadMessageCount;
    }

    @Transactional(readOnly = true)
    @Override
    public UnreadMessageCountDto countAllUnreadMessagesByUser(Long userId) {
        User user = userService.findUserById(userId);
        Set<Chat> chats = user.getChats();
        int unreadMessageCount = chats.stream()
                .mapToInt(chat -> countUnreadMessagesByChatAndUser(user, chat))
                .sum();
        return new UnreadMessageCountDto(unreadMessageCount);
    }

    private void checkUserParticipationInChat(User user, Long chatId) {
        Boolean isUserExistInChat = chatRepository.existsChatByParticipantAndId(user, chatId);
        if (!isUserExistInChat) {
            String erMessage = "User %s is not participant of chat with id %s".formatted(
                    user.getUsername(), chatId
            );
            log.error(erMessage);
            throw new UnexpectedDataException(erMessage);
        }
    }

    private void checkMessageId(Long currentReadMessageId, Long newReadMessageId) {
        if (currentReadMessageId >= newReadMessageId) {
            String erMessage = "New read message id %s is less or equal with current read message id %s".formatted(
                    newReadMessageId, currentReadMessageId
            );
            log.error(erMessage);
            throw new UnexpectedDataException(erMessage);
        }
    }
}
