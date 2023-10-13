package com.covenant.tribe.chat.service.impl;

import com.covenant.tribe.chat.domain.Chat;
import com.covenant.tribe.chat.domain.Message;
import com.covenant.tribe.chat.dto.AuthorDto;
import com.covenant.tribe.chat.dto.ChatMessageDto;
import com.covenant.tribe.chat.repository.ChatRepository;
import com.covenant.tribe.chat.repository.MessageRepository;
import com.covenant.tribe.chat.service.ChatMessageService;
import com.covenant.tribe.chat.service.ChatService;
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

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    MessageRepository messageRepository;
    ChatRepository chatRepository;
    UserService userService;

    @Transactional
    @Override
    public Page<ChatMessageDto> getMessagesByChatId(Long userId, Long chatId, Pageable pageable) {
        User user = userService.findUserById(userId);
        Boolean canUserWriteToChat = chatRepository.existsChatByParticipantAndId(user, chatId);
        if (!canUserWriteToChat) {
            String erMessage = "User %s is not participant of chat with id %s".formatted(
                    user.getUsername(), chatId
            );
            log.error(erMessage);
            throw new UnexpectedDataException(erMessage);
        }
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
                    authorDto,
                    message.getText(),
                    message.getCreatedAt()
            );
        });
    }
}
