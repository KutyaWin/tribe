package com.covenant.tribe.scheduling;

import com.covenant.tribe.scheduling.model.Broadcast;
import org.quartz.*;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public class TimerUtil {
    public static JobDetail buildJobDetail(Broadcast broadcast) {
        Class jobClass = BroadcastJob.class;
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(jobClass.getSimpleName(), broadcast);
        String s = String.valueOf(UUID.randomUUID());
        return JobBuilder
                .newJob().ofType(jobClass)
                .withIdentity(s)
                .setJobData(jobDataMap)
                .build();
    }

    public static Optional<Trigger> buildTrigger(final Broadcast broadcast) {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime repeatDate = broadcast.getRepeatDate();
        OffsetDateTime startTime = repeatDate.isAfter(now) ? repeatDate : now;
        Date offsetStartTime = Date.from(Instant.from(startTime.plus(2, ChronoUnit.SECONDS)));
        Date endTime = Date.from(broadcast.getEndDate().toInstant());
        if (!endTime.after(offsetStartTime)) return Optional.empty();
        String s = String.valueOf(UUID.randomUUID());
        return Optional.of(TriggerBuilder
                .newTrigger()
                .withIdentity(s)
                .startAt(offsetStartTime)
                .endAt(endTime)
                .build());
    }
}
