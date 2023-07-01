package com.covenant.tribe.scheduling;

import com.covenant.tribe.scheduling.model.Broadcast;
import com.covenant.tribe.scheduling.model.BroadcastEntity;
import com.covenant.tribe.scheduling.service.BroadcastService;
import com.covenant.tribe.scheduling.service.ExecuteBroadcastService;
import com.covenant.tribe.scheduling.service.SchedulerService;
import lombok.SneakyThrows;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.covenant.tribe.scheduling.BroadcastStatuses.*;

@Component
public class BroadcastJob implements Job {
    @Autowired
    private ExecuteBroadcastService executeBroadcastService;

    @Autowired
    private BroadcastService broadcastService;

    @Autowired
    private SchedulerService schedulerService;

    @SneakyThrows
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Broadcast broadcast = (Broadcast) jobExecutionContext
                .getJobDetail()
                .getJobDataMap()
                .get(BroadcastJob.class
                        .getSimpleName());

        BroadcastEntity byId = broadcastService.findById(broadcast.getBroadcastEntityId());
        TriggerKey triggerKey = jobExecutionContext.getTrigger().getKey();
        BroadcastStatuses status = byId.getStatus();
        if (status.equals(COMPLETE_SUCCESSFULLY) ||
                status.equals(FAILED_TO_COMPLETE)) {
                    schedulerService.unschedule(triggerKey);
                } else {
            if (status.equals(ENDED_WITH_ERROR) && byId.getFireCount() >= 5) {
                byId.setStatus(FAILED_TO_COMPLETE);
                broadcastService.update(byId);
            } else {
                executeBroadcastService.executeBroadcast(broadcast);
            }
        }
    }
}
