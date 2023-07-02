package com.covenant.tribe.scheduling;

import com.covenant.tribe.scheduling.model.Broadcast;
import com.covenant.tribe.scheduling.model.Notification;
import com.covenant.tribe.scheduling.notifications.NotificationStatus;
import com.covenant.tribe.scheduling.notifications.NotificationStrategyName;
import com.covenant.tribe.scheduling.service.NotificationService;
import com.covenant.tribe.scheduling.service.SchedulerService;
import org.junit.jupiter.api.Test;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Sql(value = {"/sql/users/init_users.sql", "/sql/events/init_events.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/scheduling/delete_schedule.sql",
        "/sql/events/delete_events.sql",
        "/sql/users/delete_users.sql",}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@ActiveProfiles("test")
@SpringBootTest
public class SchedulerServiceIT {

    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private NotificationService notificationService;

    @Test
    public void whenTaskIsScheduledThenItWillBeExecutedInTime() throws SchedulerException, InterruptedException {
        Broadcast bro = new Broadcast(1000L,
                OffsetDateTime.now().plus(2, ChronoUnit.SECONDS),
                OffsetDateTime.now().plus(7, ChronoUnit.SECONDS),
                NotificationStrategyName.EVENT);
        System.out.println(bro);
        Broadcast broadcast = schedulerService.schedule(bro);
        Thread.sleep(1500);
        List<Notification> messagesForBroadcast = notificationService.getMessagesForBroadcast(broadcast);
        assertEquals(0, messagesForBroadcast.size());
        Thread.sleep(1000);
        messagesForBroadcast = notificationService.getMessagesForBroadcast(broadcast);
        assertNotEquals(0, messagesForBroadcast.size());
        messagesForBroadcast.forEach(m->assertEquals(m.getStatus(), NotificationStatus.SUCCESSFULLY_SENT));
    }

    /**
     * Checks that deadlocks are not appearing
     * @throws SchedulerException
     * @throws InterruptedException
     */
    @Test
    public void whenTwoTasksScheduledThenTheyWillBeCompletedInTime() throws SchedulerException, InterruptedException {
//        given
        Broadcast bro = new Broadcast(1000L,
                OffsetDateTime.now().plus(3, ChronoUnit.SECONDS),
                OffsetDateTime.now().plus(4, ChronoUnit.SECONDS),
                NotificationStrategyName.EVENT);
        Broadcast a = schedulerService.schedule(bro);
        Broadcast bro2 = new Broadcast(1001L,
                OffsetDateTime.now().plus(4, ChronoUnit.SECONDS),
                OffsetDateTime.now().plus(6, ChronoUnit.SECONDS),
                NotificationStrategyName.EVENT);
        Broadcast b = schedulerService.schedule(bro2);

//        when
        Thread.sleep(1500); //passed 1500

        List<Notification> messagesForA = notificationService.getMessagesForBroadcast(a);
        assertEquals(messagesForA.size(), 0);
        Thread.sleep(1000); //passed 2500

        List<Notification> messagesForB = notificationService.getMessagesForBroadcast(b);
        assertEquals(messagesForB.size(), 0);
        Thread.sleep(1000); //passed 3500

//        then
        messagesForA = notificationService.getMessagesForBroadcast(a);
        assertNotEquals(0, messagesForA.size());
        messagesForA.forEach(m->assertEquals(m.getStatus(), NotificationStatus.SUCCESSFULLY_SENT));
        Thread.sleep(1000); //passed 4500

        messagesForB = notificationService.getMessagesForBroadcast(b);
        assertNotEquals(0, messagesForB.size());
        messagesForB.forEach(m->assertEquals(m.getStatus(), NotificationStatus.SUCCESSFULLY_SENT));
    }

}
