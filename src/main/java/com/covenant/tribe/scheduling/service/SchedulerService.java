package com.covenant.tribe.scheduling.service;

import com.covenant.tribe.scheduling.model.Broadcast;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;

public interface SchedulerService {
    Broadcast schedule(Broadcast broadcast) throws SchedulerException;

    void unschedule(TriggerKey triggerKey);
}
