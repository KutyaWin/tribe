package com.covenant.tribe.scheduling.service;

import com.covenant.tribe.scheduling.BroadcastStatuses;
import com.covenant.tribe.scheduling.model.Broadcast;
import com.covenant.tribe.scheduling.model.BroadcastEntity;

public interface BroadcastService {
    BroadcastEntity findById(Long id);
    BroadcastEntity create(Broadcast broadcast);

    BroadcastEntity findBySubjectIdAndStatusNot(Long eventId, BroadcastStatuses status);

    BroadcastEntity update(BroadcastEntity broadcast);

}
