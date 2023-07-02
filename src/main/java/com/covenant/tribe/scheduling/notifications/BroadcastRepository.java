package com.covenant.tribe.scheduling.notifications;

import com.covenant.tribe.scheduling.model.BroadcastEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BroadcastRepository extends JpaRepository<BroadcastEntity, Long> {

    Optional<BroadcastEntity> findBySubjectId(Long eventId);

}
