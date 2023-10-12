package com.covenant.tribe.chat.service;


import com.covenant.tribe.chat.dto.ChatDto;
import com.covenant.tribe.chat.dto.ChatMessageDto;
import com.covenant.tribe.chat.dto.PrivateChatInfoDto;
import com.covenant.tribe.chat.dto.PrivateChatInvitedUserDto;

import java.util.List;

public interface ChatService {

        PrivateChatInfoDto createPrivateChat(PrivateChatInvitedUserDto participantsDto, Long chatCreatorId);

        void sendMessageToSubscribers(Long authorId, Long chatId, String content);

        List<ChatDto> getChatsByUserId(Long userId);
}
