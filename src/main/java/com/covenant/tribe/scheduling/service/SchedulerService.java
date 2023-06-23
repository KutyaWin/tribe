package com.covenant.tribe.scheduling.service;

import com.covenant.tribe.scheduling.model.Broadcast;
import org.quartz.SchedulerException;

public interface SchedulerService {
    Broadcast schedule(Broadcast broadcast) throws SchedulerException;
}
