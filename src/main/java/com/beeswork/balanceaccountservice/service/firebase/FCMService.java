package com.beeswork.balanceaccountservice.service.firebase;

import com.beeswork.balanceaccountservice.dto.firebase.FCMNotificationDTO;
import com.google.firebase.messaging.FirebaseMessagingException;

import java.util.List;
import java.util.Map;

public interface FCMService {
    void sendNotifications(List<FCMNotificationDTO> FCMNotificationDTOs) throws FirebaseMessagingException;
    void sendNotification(FCMNotificationDTO fcmNotificationDTO) throws FirebaseMessagingException;
}