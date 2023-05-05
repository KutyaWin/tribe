package com.covenant.tribe.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FirebaseService {

    void sendNotificationsByFirebaseIds(
            List<String> firebaseIds, String title, String message, Long eventId
    ) throws FirebaseMessagingException;

}
