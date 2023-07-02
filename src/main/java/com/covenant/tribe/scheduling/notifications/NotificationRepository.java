package com.covenant.tribe.scheduling.notifications;

import com.covenant.tribe.scheduling.model.BroadcastEntity;
import com.covenant.tribe.scheduling.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByBroadcastEntity(BroadcastEntity broadcastEntity);
}
