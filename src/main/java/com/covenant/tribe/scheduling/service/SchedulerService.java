package com.covenant.tribe.scheduling.service;

import com.covenant.tribe.scheduling.model.Broadcast;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

public interface SchedulerService {
    Broadcast schedule(Broadcast broadcast) throws SchedulerException;

    void unschedule(TriggerKey triggerKey);

    void updateTriggerTime(Broadcast broadcast);

    Trigger getTrigger(TriggerKey triggerKey) throws SchedulerException;
}
