package com.covenant.tribe.service.impl;

import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.exeption.event.MessageDidntSendException;
import com.covenant.tribe.service.FirebaseService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class FirebaseServiceImpl implements FirebaseService {

    FirebaseMessaging fcm;

    @Override
    public void sendNotificationsByFirebaseIds(
            List<String> firebaseIds, String title,
            String message, Long eventId)
    {
        MulticastMessage multicastMessage = buildMulticastMessage(firebaseIds, message, title, eventId);
        try {
            fcm.sendMulticast(multicastMessage);
        } catch (FirebaseMessagingException e) {
            String errMessage = String.format("Messages dont send because firebase return: %s", e.getMessage());
            log.error(errMessage);
            throw new MessageDidntSendException(errMessage);
        }
    }

    private MulticastMessage buildMulticastMessage(List<String> firebaseIds, String message, String title, Long eventId) {
        Notification notification = Notification
                .builder()
                .setTitle(title)
                .setBody(message)
                .build();
        return MulticastMessage.builder()
                .addAllTokens(firebaseIds)
                .putData("event_id", eventId.toString())
                .setNotification(notification)
                .build();
    }

    @Override
    public void sendNotificationsToUsers(List<User> userIdsWhoWillGetNotification,
                                         boolean isEighteenYearsRestrictForEvent,
                                         String notificationTitle, String notificationMessage, Long eventIdForNotification
    ) {
        List<String> usersFirebaseIds;
        if (isEighteenYearsRestrictForEvent) {
            usersFirebaseIds = userIdsWhoWillGetNotification.stream()
                    .filter(u -> u.getBirthday() != null)
                    .filter(u -> u.getBirthday().isBefore(LocalDate.now().minusYears(18)))
                    .map(User::getFirebaseId)
                    .toList();
        } else {
            usersFirebaseIds = userIdsWhoWillGetNotification.stream()
                    .map(User::getFirebaseId)
                    .toList();
        }

        if (!usersFirebaseIds.isEmpty()) {
            sendNotificationsByFirebaseIds(usersFirebaseIds, notificationTitle, notificationMessage, eventIdForNotification);
        }
    }
}
