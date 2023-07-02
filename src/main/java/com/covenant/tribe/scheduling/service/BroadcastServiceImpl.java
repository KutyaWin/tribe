package com.covenant.tribe.scheduling.service;

import com.covenant.tribe.exeption.scheduling.BroadcastNotFoundException;
import com.covenant.tribe.scheduling.BroadcastStatuses;
import com.covenant.tribe.scheduling.model.Broadcast;
import com.covenant.tribe.scheduling.model.BroadcastEntity;
import com.covenant.tribe.scheduling.notifications.BroadcastRepository;
import com.google.common.base.Preconditions;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BroadcastServiceImpl implements BroadcastService {

    private final BroadcastRepository broadcastRepository;

    @Override
    public BroadcastEntity findById(Long id) {
        return broadcastRepository.findById(id).orElseThrow(()-> new EntityNotFoundException(String.format(
                "Broadcast with id %s was not found", id)));
    }

    @Override
    @Transactional
    public BroadcastEntity create(Broadcast broadcast) {
        Preconditions.checkState(broadcast.getSubjectId()!=null,
                "Broadcast does not have associated resource");
        BroadcastEntity build = BroadcastEntity.builder().status(BroadcastStatuses.NEW)
                .startTime(broadcast.getRepeatDate())
                .repeatTime(broadcast.getRepeatDate())
                .subjectId(broadcast.getSubjectId())
                .endTime(broadcast.getEndDate())
                .notificationsCreated(false)
                .notificationStrategyName(broadcast.getNotificationStrategyName())
                .messageStrategyName(broadcast.getMessageStrategyName())
                .build();
        return broadcastRepository.save(build);
    }

    @Override
    public BroadcastEntity findBySubjectId(Long eventId) {
        return broadcastRepository
                .findBySubjectId(eventId)
                .orElseThrow(() -> {
                    String message = String.format(
                            "Broadcast with eventId %s doesn't exist'", eventId
                    );
                    log.error(message);
                    return new BroadcastNotFoundException(message);
                });
    }

    @Override
    public BroadcastEntity update(BroadcastEntity broadcast) {
        return broadcastRepository.save(broadcast);
    }
}
