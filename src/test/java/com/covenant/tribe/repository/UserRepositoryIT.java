package com.covenant.tribe.repository;

import com.covenant.tribe.AbstractTestcontainers;
import com.covenant.tribe.domain.user.RelationshipStatus;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.domain.user.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Sql(value = "/sql/users/init_users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/sql/users/delete_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryIT extends AbstractTestcontainers {

    @Autowired
    UserRepository userRepository;

    @Test
    void findUserByIdFetchEventsWhereUserAsOrganizer() {
        Long userId  = 1000L;

        Optional<User> user = userRepository.findUserByIdFetchEventsWhereUserAsOrganizer(userId);

        assertNotNull(user);
        assertEquals(userId, user.get().getId());
    }
    @Test
    void findUserByIdAndStatus() {
        Long userId  = 1000L;

        Optional<User> user = userRepository.findUserByIdAndStatus(userId, UserStatus.ENABLED);

        assertNotNull(user);
        assertEquals(userId, user.get().getId());
        assertEquals(UserStatus.ENABLED, user.get().getStatus());
    }

    @Test
    void findAllByUsernameContains() {
        String partialUsername = "ala";
        UserStatus userStatus = UserStatus.ENABLED;

        Page<User> page = userRepository.findAllByUsernameContains(partialUsername, userStatus, Pageable.unpaged());

        assertNotNull(page);
        assertEquals(page.get().count(), 2);

    }

    @Test
    void findAllSubscribersByPartialUsername() {
        long userId  = 1000L;
        String username = "ala";
        RelationshipStatus status = RelationshipStatus.SUBSCRIBE;

        Page<User> allSubscribers = userRepository.findAllSubscribersByPartialUsername(userId, username, status, Pageable.unpaged());

        assertNotNull(allSubscribers);
        assertEquals(allSubscribers.get().count(), 1);
    }

    @Test
    void findAllNotFollowingUser() {
        long userId  = 1001L;
        RelationshipStatus status = RelationshipStatus.SUBSCRIBE;

        Page<User> allNotFollowingUser = userRepository.findAllNotFollowingUser(userId, status, Pageable.unpaged());

        assertNotNull(allNotFollowingUser);
        assertEquals(allNotFollowingUser.get().count(), 1);
    }

    @Test
    void findAllNotFollowingUserByPartialUsername() {
        long userId  = 1001L;
        RelationshipStatus status = RelationshipStatus.SUBSCRIBE;
        String unsubscriberUsername = "ala";

        Page<User> allNotFollowingUser = userRepository
                .findAllNotFollowingUserByPartialUsername(unsubscriberUsername, userId, status, Pageable.unpaged());

        assertNotNull(allNotFollowingUser);
        assertEquals(allNotFollowingUser.get().count(), 1);
    }

    @Test
    void findAllSubscribers() {
        long userId  = 1000L;
        RelationshipStatus status = RelationshipStatus.SUBSCRIBE;

        Page<User> allSubscribers = userRepository.findAllSubscribers(userId, status, Pageable.unpaged());

        assertNotNull(allSubscribers);
        assertEquals(allSubscribers.get().count(), 1);
    }
    @Test
    void findMutuallySubscribed() {
        long userId  = 1001L;
        List<Long> userIds = List.of(1000L, 1002L);

        Set<Long> allSubscribers = userRepository.findMutuallySubscribed(userIds, userId);

        assertNotNull(allSubscribers);
        assertEquals(allSubscribers.size(), 1);
    }

    @Test
    void findUserByUsername() {
        String username = "alam";

        Optional<User> user = userRepository.findUserByUsername(username);

        assertNotNull(user);
        assertEquals(username, user.get().getUsername());
    }

    @Test
    void findUserByUserEmail() {
        String email = "test1@gmail.com";

        Optional<User> user = userRepository.findUserByUserEmail(email);

        assertNotNull(user);
        assertEquals(email, user.get().getUserEmail());
    }

    @Test
    void findUserByPhoneNumber() {
        String phoneNumber = "1234560";

        User user = userRepository.findUserByPhoneNumber(phoneNumber);

        assertNotNull(user);
        assertEquals(phoneNumber, user.getPhoneNumber());
    }

    @Test
    void findAllByIdInAndStatus() {
        List<Long> ids = List.of(1000L, 1001L, 1002L);
        UserStatus status = UserStatus.ENABLED;

        List<User> users = userRepository.findAllByIdInAndStatus(ids, status);

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals(ids.get(0), users.get(0).getId());
        assertEquals(ids.get(1), users.get(1).getId());

    }

    @Test
    void findByGoogleId() {

        String googleId = "testG1";

        User user = userRepository.findByGoogleId(googleId);

        assertNotNull(user);
        assertEquals(googleId, user.getGoogleId());
    }

    @Test
    void findByVkId() {

        String vkId = "testV1";

        User user = userRepository.findByVkId(vkId);

        assertNotNull(user);
        assertEquals(vkId, user.getVkId());
    }

    @Test
    void findAllByInterestingEventTypeContainingAndStatus() {
        Long eventType = 1000L;
        String userStatus = UserStatus.ENABLED.toString();

        List<User> users = userRepository.findAllByInterestingEventTypeContainingAndStatus(eventType, userStatus);

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals(eventType, users.get(0).getId());

    }

    @Test
    void existsUserByUserEmail() {
            String email = "test1@gmail.com";

            boolean exists = userRepository.existsUserByUserEmail(email);

            assertTrue(exists);

            String lieEmail = "test@gmail.com";

            boolean lieExists = userRepository.existsUserByUserEmail(lieEmail);

            assertFalse(lieExists);
    }

    @Test
    void existsUserByUsername() {
            String username = "alam";

            boolean exists = userRepository.existsUserByUsername(username);

            assertTrue(exists);

            String lieUsername = "pikachu";

            boolean lieExists = userRepository.existsUserByUsername(lieUsername);

            assertFalse(lieExists);
    }


    @Test
    void existsUserByPhoneNumber() {

        String phone = "1234560";

        boolean exists = userRepository.existsUserByPhoneNumber(phone);

        assertTrue(exists);

        String liePhone = "1234";

        boolean lieExists = userRepository.existsUserByPhoneNumber(liePhone);

        assertFalse(lieExists);

    }
}