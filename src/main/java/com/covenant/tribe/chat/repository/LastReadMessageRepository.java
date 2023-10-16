package com.covenant.tribe.chat.repository;

import com.covenant.tribe.chat.domain.LastReadMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LastReadMessageRepository extends JpaRepository<LastReadMessage, Long> {
    Optional<LastReadMessage> findByChatIdAndParticipantId(Long chatId, Long userId);
}
