package com.covenant.tribe.chat.repository;

import com.covenant.tribe.chat.domain.Chat;
import com.covenant.tribe.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findAllByParticipantInAndIsGroup(Set<User> participants, Boolean isGroup);
}
