package com.covenant.tribe.scheduling.service;

import com.covenant.tribe.exeption.scheduling.TriggerNotUpdatedException;
import com.covenant.tribe.scheduling.BroadcastStatuses;
import com.covenant.tribe.scheduling.TimerUtil;
import com.covenant.tribe.scheduling.model.Broadcast;
import com.covenant.tribe.scheduling.model.BroadcastEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
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
        broadcastEntity.setTriggerKey(trigger.getKey().getName());
        broadcastService.update(broadcastEntity);
        scheduler.scheduleJob(jobDetail,trigger);
        return broadcast;
    }

    @Override
    public void unschedule(TriggerKey triggerKey) {
        try {
            scheduler.unscheduleJob(triggerKey);
        } catch (SchedulerException e) {
            String message = String.format(
                    "Cannot unschedule trigger %s, with exception: %s",
                    triggerKey, e.getMessage()
            );
            log.error(message);
        }
    }

    @Override
    public void updateTriggerTime(Broadcast broadcast) {
        BroadcastEntity broadcastEntity = broadcastService
                .findBySubjectIdAndStatusNot(broadcast.getSubjectId(), BroadcastStatuses.COMPLETE_SUCCESSFULLY);
        broadcast.setBroadcastEntityId(broadcastEntity.getId());
        try {
            Trigger newTrigger = TimerUtil.buildTrigger(broadcast)
                    .orElseThrow(()->new DateTimeException("Broadcast start is after end"));
            Trigger oldTrigger = scheduler.getTrigger(new TriggerKey(broadcastEntity.getTriggerKey()));
            scheduler.rescheduleJob(oldTrigger.getKey(), newTrigger);
            broadcastEntity.setStartTime(broadcast.getRepeatDate());
            broadcastEntity.setEndTime(broadcast.getEndDate());
            broadcastEntity.setTriggerKey(newTrigger.getKey().getName());
            broadcastEntity.setStatus(BroadcastStatuses.NEW);
            broadcastService.update(broadcastEntity);
        } catch(SchedulerException e) {
            String message = String.format(
                    "Trigger didn't update because: %s'", e.getMessage()
            );
            log.error(message);
            throw new TriggerNotUpdatedException(message);
        }

    }

    @Override
    public Trigger getTrigger(TriggerKey triggerKey) throws SchedulerException {
        return scheduler.getTrigger(triggerKey);
    }
}
