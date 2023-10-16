package com.covenant.tribe.chat.service;

import com.covenant.tribe.chat.dto.ChatMessageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatMessageService {

    Page<ChatMessageDto> getMessagesByChatId(Long userId, Long chatId, Pageable pageable);

    void setLastReadMessage(Long userId, Long chatId, Long messageId);
}
