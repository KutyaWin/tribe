package com.covenant.tribe.chat.service;


import com.covenant.tribe.chat.domain.Chat;
import com.covenant.tribe.chat.dto.ChatDto;
import com.covenant.tribe.chat.dto.PrivateChatInfoDto;
import com.covenant.tribe.chat.dto.PrivateChatInvitedUserDto;
import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatService {

        PrivateChatInfoDto createPrivateChat(PrivateChatInvitedUserDto participantsDto, Long chatCreatorId);

        Long createEventChat(User eventOrganizerId, Event eventId);

        void sendMessageToSubscribers(Long authorId, Long chatId, String content);

        Page<ChatDto> getChatsByUserId(Long userId, Pageable pageable);

        Chat getChatByEventId(Long eventId);

        void addParticipantToEventChat(Long eventId, User user);

        void deleteUserFromEventChat(Long eventId, Long userId);
}
