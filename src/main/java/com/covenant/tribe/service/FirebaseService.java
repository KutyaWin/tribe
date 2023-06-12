package com.covenant.tribe.service;

import com.covenant.tribe.domain.user.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FirebaseService {

    void sendNotificationsByFirebaseIds(
            List<String> firebaseIds, String title,
            String message, Long eventId);

    void sendNotificationsToUsers(List<User> userIdsWhoWillGetNotification,
                                  boolean isEighteenYearsRestrictForEvent,
                                  String notificationTitle, String notificationMessage, Long eventIdForNotification);
}
