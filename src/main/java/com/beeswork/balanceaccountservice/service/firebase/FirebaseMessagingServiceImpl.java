//package com.beeswork.balanceaccountservice.service.firebase;
//
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.messaging.FirebaseMessagingException;
//import com.google.firebase.messaging.Message;
//import org.springframework.stereotype.Service;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.Random;
//
//@Service
//public class FirebaseMessagingServiceImpl implements FirebaseMessagingService {
//
//    public FirebaseMessagingServiceImpl() throws IOException {
//        FileInputStream serviceAccount = new FileInputStream(
//                "/Users/kisungtae/Documents/intellijSpringProjects/balance-896d6-firebase-adminsdk-sppjt-13d87a9365.json");
//
//        FirebaseOptions options = new FirebaseOptions.Builder()
//                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                .setDatabaseUrl("https://balance-896d6.firebaseio.com")
//                .build();
//
//        FirebaseApp.initializeApp(options);
//    }
//
//    @Override
//    public void sendNotification(String token, String message) throws FirebaseMessagingException {
//
//        Random random = new Random();
//        Message fcmMessage = Message.builder()
//                                    .putData("message", message)
//                                    .setToken(token)
//                                    .build();
//
//        String response = FirebaseMessaging.getInstance().send(fcmMessage);
//        System.out.println("response: " + response);
//
//    }
//}
