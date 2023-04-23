package com.covenant.tribe.repository;

import com.covenant.tribe.domain.UserRelationsWithEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRelationsWithEventRepository extends JpaRepository<UserRelationsWithEvent, Long> {

    Optional<UserRelationsWithEvent> getByUserIdAndEventId(Long userId, Long eventId);
}
