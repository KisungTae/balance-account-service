package com.beeswork.balanceaccountservice.service.fcm;

import com.beeswork.balanceaccountservice.dao.pushtoken.PushTokenDAO;
import com.beeswork.balanceaccountservice.dto.push.Notification;
import com.beeswork.balanceaccountservice.dto.swipe.ClickDTO;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Locale;


@Service
public class FCMServiceImpl implements FCMService {

    private final FirebaseMessaging firebaseMessaging;
    private final MessageSource     messageSource;
    private final Message.Builder   messageBuilder;
    private final PushTokenDAO pushTokenDAO;

    private static final String CLICKED_NOTIFICATION_TITLE = "clicked.notification.title";
    private static final String MATCHED_NOTIFICATION_TITLE = "matched.notification.title";

    private static final String SWIPER_NAME      = "swiperName";
    private static final String SWIPER_PHOTO_KEY = "swiperPhotoKey";
    private static final String CLICK_RESULT     = "clickResult";

    @Autowired
    public FCMServiceImpl(FirebaseMessaging firebaseMessaging,
                          MessageSource messageSource,
                          Message.Builder messageBuilder,
                          PushTokenDAO pushTokenDAO) {
        this.firebaseMessaging = firebaseMessaging;
        this.messageSource = messageSource;
        this.messageBuilder = messageBuilder;
        this.pushTokenDAO = pushTokenDAO;
    }


    @Override
    @Async("processExecutor")
    public void sendNotification(Notification notification, Locale locale) {
        try {

            if (notification.validateFCMToken())
                firebaseMessaging.send(notification.buildFCMMessage(messageBuilder, messageSource, locale));
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    @Async("processExecutor")
    public void sendNotification(ClickDTO clickDTO, Locale locale) {
//        switch (clickDTO.getResult()) {
//            case CLICK:
//                sendNotification(clickedMessageBuilder(clickDTO, locale));
//            case MATCH:
//                sendNotification(matchedMessageBuilder(clickDTO, locale));
//            default:
//                break;
//        }
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
//        messageBuilder.setToken(clickDTO.getSwipedFCMToken());
//        messageBuilder.putData(CLICK_RESULT, clickDTO.getResult().toString());
//        messageBuilder.putData(SWIPER_NAME, clickDTO.getSwiperName());
//        messageBuilder.putData(SWIPER_PHOTO_KEY, clickDTO.getSwiperPhotoKey());
        return messageBuilder;
    }

    private void sendNotification(Message.Builder messageBuilder) {
        try {
            firebaseMessaging.send(messageBuilder.build());
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }

    private com.google.firebase.messaging.Notification notification(String titleKey, Locale locale) {
        String title = messageSource.getMessage(titleKey, null, locale);
        return com.google.firebase.messaging.Notification.builder().setTitle(title).setBody("").build();
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
