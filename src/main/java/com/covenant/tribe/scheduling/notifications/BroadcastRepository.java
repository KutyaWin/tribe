package com.covenant.tribe.scheduling.notifications;

import com.covenant.tribe.scheduling.model.BroadcastEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BroadcastRepository extends JpaRepository<BroadcastEntity, Long> {
}
