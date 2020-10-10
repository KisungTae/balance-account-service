package com.beeswork.balanceaccountservice.service.firebase;

import com.beeswork.balanceaccountservice.dto.firebase.FirebaseNotificationDTO;
import com.google.firebase.messaging.FirebaseMessagingException;

import java.util.List;

public interface FirebaseService {
    void sendNotification(FirebaseNotificationDTO firebaseNotificationDTO) throws FirebaseMessagingException;
}