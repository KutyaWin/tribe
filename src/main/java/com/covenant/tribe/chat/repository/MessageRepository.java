package com.covenant.tribe.chat.repository;

import com.covenant.tribe.chat.domain.Chat;
import com.covenant.tribe.chat.domain.Message;
import com.covenant.tribe.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Message findFirstByChatOrderByCreatedAtDesc(Chat chat);

    Page<Message> findAllByChatIdOrderByCreatedAtDesc(Long chatId, Pageable pageable);

    int countMessageByAuthorNotAndChatAndIdAfter(User author, Chat chat, Long messageId);
    int countMessageByAuthorNotAndChat(User author, Chat chat);
}
