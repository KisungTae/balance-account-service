package com.beeswork.balanceaccountservice.service.firebase;

import com.beeswork.balanceaccountservice.dto.firebase.FCMNotificationDTO;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class FirebaseServiceImpl implements FirebaseService {

    private final FirebaseMessaging firebaseMessaging;

    @Autowired
    public FirebaseServiceImpl(FirebaseMessaging firebaseMessaging) {
        this.firebaseMessaging = firebaseMessaging;
    }

    @Override
    @Async("processExecutor")
    public void sendNotification(FCMNotificationDTO fcmNotificationDTO)
    throws FirebaseMessagingException {

        sendNotification(fcmNotificationDTO.getToken(), fcmNotificationDTO.getData());
    }

    private void sendNotification(String token, Map<String, String> data)
    throws FirebaseMessagingException {

        Message.Builder messageBuilder = Message.builder();
        messageBuilder.setToken(token);
        for (Map.Entry<String, String> entry : data.entrySet()) {
            messageBuilder.putData(entry.getKey(), entry.getValue());
        }
        Notification notification = Notification.builder()
                                                .setTitle("balance notification title")
                                                .setBody("balance notification body").build();

        messageBuilder.setNotification(notification);
        String response = firebaseMessaging.send(messageBuilder.build());
        System.out.println("response: " + response);
    }

}
