package com.covenant.tribe.chat.service;


import com.covenant.tribe.chat.dto.ChatDto;
import com.covenant.tribe.chat.dto.ChatMessageDto;
import com.covenant.tribe.chat.dto.PrivateChatInfoDto;
import com.covenant.tribe.chat.dto.PrivateChatInvitedUserDto;
import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatService {

        PrivateChatInfoDto createPrivateChat(PrivateChatInvitedUserDto participantsDto, Long chatCreatorId);

        Long createEventChat(User eventOrganizerId, Event eventId);

        void sendMessageToSubscribers(Long authorId, Long chatId, String content);

        Page<ChatDto> getChatsByUserId(Long userId, Pageable pageable);
}
