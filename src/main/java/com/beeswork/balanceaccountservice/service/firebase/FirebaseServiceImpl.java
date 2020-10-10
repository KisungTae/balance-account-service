package com.beeswork.balanceaccountservice.service.firebase;

import com.beeswork.balanceaccountservice.config.properties.FirebaseProperties;
import com.beeswork.balanceaccountservice.constant.FirebaseConstant;
import com.beeswork.balanceaccountservice.dto.firebase.FirebaseNotificationDTO;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;


@Service
public class FirebaseServiceImpl implements FirebaseService {

    private final FirebaseProperties firebaseProperties;

    @Autowired
    public FirebaseServiceImpl(FirebaseProperties firebaseProperties)
    throws IOException {

        this.firebaseProperties = firebaseProperties;
        setServiceAccount();
    }

    private void setServiceAccount() throws IOException {

        FileInputStream serviceAccount = new FileInputStream(firebaseProperties.getServiceAccountKeyPath());

        FirebaseOptions options = FirebaseOptions.builder()
                                                 .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                                                 .setDatabaseUrl(firebaseProperties.getDatabaseURL())
                                                 .build();

        FirebaseApp.initializeApp(options);
    }

    public void sendNotification(FirebaseNotificationDTO firebaseNotificationDTO) throws FirebaseMessagingException {

        for (String token : firebaseNotificationDTO.getTokens()) {
            sendNotification(token,
                             firebaseNotificationDTO.getNotificationType(),
                             firebaseNotificationDTO.getMessage());
        }
    }

    private void sendNotification(String token, String notificationType, String message)
    throws FirebaseMessagingException {
        Message firebaseMessage = Message.builder()
                                    .putData(FirebaseConstant.NotificationDataKey.NOTIFICATION_TYPE, notificationType)
                                    .putData(FirebaseConstant.NotificationDataKey.MESSAGE, message)
                                    .setToken(token)
                                    .build();

        String response = FirebaseMessaging.getInstance().send(firebaseMessage);
        System.out.println("response: " + response);

    }

}
