package com.covenant.tribe.scheduling.service;

import com.covenant.tribe.scheduling.TimerUtil;
import com.covenant.tribe.scheduling.model.Broadcast;
import com.covenant.tribe.scheduling.model.BroadcastEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.stereotype.Component;

import java.time.DateTimeException;

@Slf4j
@RequiredArgsConstructor
@Component
public class QuartzSchedulerService implements SchedulerService{
    private final Scheduler scheduler;

    private final BroadcastService broadcastService;

    /**
     * @Transactional not recommended on this method
     */
    @Override
    public Broadcast schedule(Broadcast broadcast) throws SchedulerException {
        BroadcastEntity broadcastEntity = broadcastService.create(broadcast);
        broadcast.setBroadcastEntityId(broadcastEntity.getId());
        JobDetail jobDetail = TimerUtil.buildJobDetail(broadcast);
        Trigger trigger = TimerUtil.buildTrigger(broadcast)
                .orElseThrow(()->new DateTimeException("Broadcast start is after end"));
        scheduler.scheduleJob(jobDetail,trigger);
        return broadcast;
    }
}
