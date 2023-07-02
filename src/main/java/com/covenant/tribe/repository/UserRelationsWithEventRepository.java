package com.covenant.tribe.repository;

import com.covenant.tribe.domain.UserRelationsWithEvent;
import com.covenant.tribe.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRelationsWithEventRepository extends JpaRepository<UserRelationsWithEvent, Long> {

    Optional<UserRelationsWithEvent> findByUserRelationsIdAndEventRelationsId(Long userId, Long eventId);
    Optional<UserRelationsWithEvent> findByUserRelationsIdAndEventRelationsIdAndIsInvitedTrue(Long userId, Long eventId);
    Optional<UserRelationsWithEvent> findByUserRelationsIdAndEventRelationsIdAndIsParticipantTrue(Long userId, Long eventId);
    Optional<UserRelationsWithEvent> findByUserRelationsIdAndEventRelationsIdAndIsWantToGoTrue(Long userId, Long eventId);
    List<UserRelationsWithEvent> findByEventRelationsIdAndIsWantToGo(Long eventId, boolean wantToGo);
    List<UserRelationsWithEvent> findAllByUserRelations(User user);
    List<UserRelationsWithEvent> findByEventRelationsId(Long eventId);
}
