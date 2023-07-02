package com.covenant.tribe.scheduling.service;

import com.covenant.tribe.scheduling.model.Broadcast;
import org.quartz.SchedulerException;

public interface ExecuteBroadcastService {
    void executeBroadcast(Broadcast broadcast);
}
