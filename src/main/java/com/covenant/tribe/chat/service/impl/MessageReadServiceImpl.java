package com.covenant.tribe.chat.service.impl;

import com.covenant.tribe.chat.domain.Chat;
import com.covenant.tribe.chat.domain.LastReadMessage;
import com.covenant.tribe.chat.dto.LastReadMessageDto;
import com.covenant.tribe.chat.service.MessageReadService;
import com.covenant.tribe.domain.user.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessageReadServiceImpl implements MessageReadService {

    SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void sendMessageToSubscribers(User userWhoRead, Chat chatWhenRead, LastReadMessage readMessage) {
        chatWhenRead.getParticipant().stream()
                .filter(participant -> !participant.equals(userWhoRead))
                .forEach(participant -> {
                    String destination = "/topic/" + participant.getId() + "/message/read";
                    LastReadMessageDto lastReadMessageDto = new LastReadMessageDto(
                            userWhoRead.getId(),
                            readMessage.getMessage().getId(),
                            chatWhenRead.getId()
                    );
                    simpMessagingTemplate.convertAndSend(
                            destination,
                            lastReadMessageDto
                    );
                });
    }
}
