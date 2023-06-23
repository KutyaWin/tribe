package com.covenant.tribe.scheduling.service;

import com.covenant.tribe.scheduling.BroadcastStatuses;
import com.covenant.tribe.scheduling.model.Broadcast;
import com.covenant.tribe.scheduling.model.BroadcastEntity;
import com.covenant.tribe.scheduling.notifications.BroadcastRepository;
import com.google.common.base.Preconditions;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
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
}
