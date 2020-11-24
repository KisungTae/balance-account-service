package com.beeswork.balanceaccountservice.service.firebase;

import com.beeswork.balanceaccountservice.dto.swipe.ClickDTO;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Locale;


@Service
public class FirebaseServiceImpl implements FirebaseService {

    private final FirebaseMessaging firebaseMessaging;
    private final MessageSource messageSource;

    private static final String CLICKED_NOTIFICATION_TITLE = "clicked.notification.title";
    private static final String MATCHED_NOTIFICATION_TITLE = "matched.notification.title";

    private static final String SWIPER_NAME = "swiperName";
    private static final String SWIPER_PHOTO_KEY = "swiperPhotoKey";
    private static final String CLICK_RESULT = "clickResult";

    @Autowired
    public FirebaseServiceImpl(FirebaseMessaging firebaseMessaging, MessageSource messageSource) {
        this.firebaseMessaging = firebaseMessaging;
        this.messageSource = messageSource;
    }

    @Override
    @Async("processExecutor")
    public void sendNotification(ClickDTO clickDTO, Locale locale) {

        switch (clickDTO.getClickResult()) {
            case CLICKED: sendNotification(clickedMessageBuilder(clickDTO, locale));
            case MATCHED: sendNotification(matchedMessageBuilder(clickDTO, locale));
            default: break;
        }
    }

    private Message.Builder clickedMessageBuilder(ClickDTO clickDTO, Locale locale) {

        Message.Builder messageBuilder = clickMessageBuilder(clickDTO);
        messageBuilder.setNotification(notification(CLICKED_NOTIFICATION_TITLE, locale));
        return messageBuilder;
    }

    private Message.Builder matchedMessageBuilder(ClickDTO clickDTO, Locale locale) {

        Message.Builder messageBuilder = clickMessageBuilder(clickDTO);
        messageBuilder.setNotification(notification(MATCHED_NOTIFICATION_TITLE, locale));
        return messageBuilder;
    }

    private Message.Builder clickMessageBuilder(ClickDTO clickDTO) {

        Message.Builder messageBuilder = Message.builder();
        messageBuilder.setToken(clickDTO.getSwipedFCMToken());
        messageBuilder.putData(CLICK_RESULT, clickDTO.getClickResult().toString());
        messageBuilder.putData(SWIPER_NAME, clickDTO.getSwiperName());
        messageBuilder.putData(SWIPER_PHOTO_KEY, clickDTO.getSwiperPhotoKey());
        return messageBuilder;
    }

    private void sendNotification(Message.Builder messageBuilder) {
        try {
            firebaseMessaging.send(messageBuilder.build());
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }

    private Notification notification(String titleKey, Locale locale) {
        String title = messageSource.getMessage(titleKey, null, locale);
        return Notification.builder().setTitle(title).setBody("").build();
    }

//    private void sendNotification(String token, Map<String, String> data)
//    throws FirebaseMessagingException {
//
//        Message.Builder messageBuilder = Message.builder();
//        messageBuilder.setToken(token);
//        for (Map.Entry<String, String> entry : data.entrySet()) {
//            messageBuilder.putData(entry.getKey(), entry.getValue());
//        }
//
//        Notification notification = Notification.builder()
//                                                .setTitle("balance notification title")
//                                                .setBody("balance notification body").build();
//
//        messageBuilder.setNotification(notification);
//        String response = firebaseMessaging.send(messageBuilder.build());
//        System.out.println("response: " + response);
//    }

}
