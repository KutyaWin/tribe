package com.covenant.tribe.scheduling;

import com.covenant.tribe.scheduling.model.Broadcast;
import com.covenant.tribe.scheduling.service.ExecuteBroadcastService;
import com.covenant.tribe.scheduling.service.SchedulerService;
import lombok.SneakyThrows;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BroadcastJob implements Job {
    @Autowired
    private ExecuteBroadcastService executeBroadcastService;

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
        if (!broadcast.getStatus().equals(BroadcastStatuses.COMPLETE_SUCCESSFULLY)) {
            executeBroadcastService.executeBroadcast(broadcast);
        } else {
            TriggerKey triggerKey = jobExecutionContext.getTrigger().getKey();
            schedulerService.unschedule(triggerKey);
        }
    }
}
