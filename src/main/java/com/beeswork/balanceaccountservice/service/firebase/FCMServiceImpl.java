package com.beeswork.balanceaccountservice.service.firebase;

import com.beeswork.balanceaccountservice.config.properties.FirebaseProperties;
import com.beeswork.balanceaccountservice.dto.firebase.FCMNotificationDTO;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;


@Service
public class FCMServiceImpl implements FCMService {

    public void sendNotification(FCMNotificationDTO fcmNotificationDTO) throws FirebaseMessagingException {

        sendNotification(fcmNotificationDTO.getToken(), fcmNotificationDTO.getMessages());
    }

    public void sendNotifications(List<FCMNotificationDTO> fcmNotificationDTOs) throws FirebaseMessagingException {

        for (FCMNotificationDTO fcmNotificationDTO : fcmNotificationDTOs) {
            sendNotification(fcmNotificationDTO.getToken(), fcmNotificationDTO.getMessages());
        }
    }

    private void sendNotification(String token, Map<String, String> messages)
    throws FirebaseMessagingException {

        Message.Builder messageBuilder = Message.builder();
        messageBuilder.setToken(token);
        for (Map.Entry<String, String> entry : messages.entrySet()) {
            messageBuilder.putData(entry.getKey(), entry.getValue());
        }
        Notification notification = Notification.builder()
                                                .setTitle("balance notification title")
                                                .setBody("balance notification body").build();
        messageBuilder.setNotification(notification);
        String response = FirebaseMessaging.getInstance().send(messageBuilder.build());
        System.out.println("response: " + response);
    }

}
