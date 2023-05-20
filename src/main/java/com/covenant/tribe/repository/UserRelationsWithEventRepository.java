package com.covenant.tribe.repository;

import com.covenant.tribe.domain.UserRelationsWithEvent;
import com.covenant.tribe.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRelationsWithEventRepository extends JpaRepository<UserRelationsWithEvent, Long> {

    Optional<UserRelationsWithEvent> getByUserRelationsIdAndEventRelationsId(Long userId, Long eventId);
    Optional<UserRelationsWithEvent> findByUserRelationsIdAndEventRelationsIdAndIsInvitedTrue(Long userId, Long eventId);
    Optional<UserRelationsWithEvent> findByUserRelationsIdAndEventRelationsIdAndIsParticipantTrue(Long userId, Long eventId);
    List<UserRelationsWithEvent> findAllByUserRelations(User user);
}
