package com.covenant.tribe.chat.service;


import com.covenant.tribe.chat.dto.ChatDto;
import com.covenant.tribe.chat.dto.ChatMessageDto;
import com.covenant.tribe.chat.dto.PrivateChatInfoDto;
import com.covenant.tribe.chat.dto.PrivateChatInvitedUserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatService {

        PrivateChatInfoDto createPrivateChat(PrivateChatInvitedUserDto participantsDto, Long chatCreatorId);

        void sendMessageToSubscribers(Long authorId, Long chatId, String content);

        Page<ChatDto> getChatsByUserId(Long userId, Pageable pageable);
}
