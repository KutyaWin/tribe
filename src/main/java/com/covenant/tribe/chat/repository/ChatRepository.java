package com.covenant.tribe.chat.repository;

import com.covenant.tribe.chat.domain.Chat;
import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("""
                select c.id
                from Chat c
                join c.participant p
                where p IN :participants
                and c.isGroup = :is_group
                group by c.id
                having count(c.id) = 2
            """)
    List<Long> findAllByParticipantInAndIsGroup(
            @Param("participants") Set<User> participants,
            @Param("is_group") Boolean isGroup
    );

    Page<Chat> findAllByParticipantId(
            Long userId, Pageable pageable
    );

    Boolean existsChatByParticipantAndId(User participant, Long chatId);

    Optional<Chat> findByEvent(Event event);

}
