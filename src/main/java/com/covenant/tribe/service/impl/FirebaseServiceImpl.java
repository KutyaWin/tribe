package com.covenant.tribe.service.impl;

import com.covenant.tribe.service.FirebaseService;
import com.google.firebase.messaging.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FirebaseServiceImpl implements FirebaseService {

    FirebaseMessaging fcm;

    @Override
    public void sendNotificationsByFirebaseIds(
            List<String> firebaseIds, String title, String message, Long eventId
    ) throws FirebaseMessagingException {
        MulticastMessage multicastMessage = buildMulticastMessage(
                firebaseIds, message, title, eventId
        );
        fcm.sendMulticast(multicastMessage);

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
}
