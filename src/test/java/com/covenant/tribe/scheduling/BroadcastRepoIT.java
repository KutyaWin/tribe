package com.covenant.tribe.scheduling;

import com.covenant.tribe.IDUtil;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.domain.user.UserStatus;
import com.covenant.tribe.scheduling.model.Broadcast;
import com.covenant.tribe.scheduling.model.BroadcastEntity;
import com.covenant.tribe.scheduling.notifications.BroadcastRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BroadcastRepoIT {

    private volatile Long broadcastId;

    @Autowired
    private BroadcastRepository broadcastRepository;

    @BeforeAll
    void init() {
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder().firebaseId(String.valueOf(23131))
                .userEmail("wow").username("e").status(UserStatus.ENABLED).build();

        var bro = BroadcastEntity.builder()
                .startTime(now)
                .repeatTime(now)
                .endTime(now.plusSeconds(4))
                .status(BroadcastStatuses.NEW)
                .notificationsCreated(false).build();

        broadcastId=broadcastRepository.save(bro).getId();

    }

    @Test
    void whenContextLoads_thenNoExceptions() {

    }

    @Test
    void givenExistingEntity_whenItIsQueriedById_thenItIsAbtainable() {
        // When
        BroadcastEntity broadcastEntity = broadcastRepository.findById(broadcastId).orElseThrow();
        // Then
        assertNotNull(broadcastEntity.getId());
    }

    @Test
    public final void givenResourceDoesNotExist_whenResourceIsRetrieved_thenNoResourceIsReceived() {
        // When
        var createdResource = broadcastRepository.findById(IDUtil.randomPositiveLong()).orElse(null);

        // Then
        assertNull(createdResource);
    }

    @AfterAll
    void destroy() {
        broadcastRepository.deleteById(broadcastId);
    }


}
