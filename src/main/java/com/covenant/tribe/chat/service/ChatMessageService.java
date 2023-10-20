package com.covenant.tribe.chat.service;

import com.covenant.tribe.chat.domain.Chat;
import com.covenant.tribe.chat.dto.ChatMessageDto;
import com.covenant.tribe.chat.dto.UnreadMessageCountDto;
import com.covenant.tribe.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatMessageService {

    Page<ChatMessageDto> getMessagesByChatId(Long userId, Long chatId, Pageable pageable);

    void setLastReadMessage(Long userId, Long chatId, Long messageId);

    int countUnreadMessagesByChatAndUser(User user, Chat chat);
    UnreadMessageCountDto countAllUnreadMessagesByUser(Long userId);
}
