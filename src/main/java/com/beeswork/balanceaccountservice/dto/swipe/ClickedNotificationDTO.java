package com.beeswork.balanceaccountservice.dto.swipe;

import com.beeswork.balanceaccountservice.dto.firebase.FirebaseNotification;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

@AllArgsConstructor
public class ClickedNotificationDTO extends FirebaseNotification {

    private static final String NOTIFICATION_BODY_CODE = "clicked.notification.body";
    private static final String SWIPER_ID = "swiper_id";
    private static final String SWIPER_NAME = "swiper_id";
    private static final String SWIPER_PHOTO_KEY = "swiper_id";
    private static final String SWIPE_UPDATED_AT = "swipeUpdatedAt";



    private final String swiperId;
    private final String swiperName;
    private final String swiperPhotoKey;
    private final Date swipeUpdatedAt;
    private final String recipientFCMToken;


    @Override
    public Message buildMessage(MessageSource messageSource, Locale locale) {
        Message.Builder messageBuilder = Message.builder();
        setNotification(messageBuilder, messageSource, locale, NOTIFICATION_BODY_CODE, null, Type.CLICKED);
        messageBuilder.setToken(this.recipientFCMToken);
        messageBuilder.putData(SWIPER_ID, swiperId);
        messageBuilder.putData(SWIPER_NAME, swiperName);
        messageBuilder.putData(SWIPER_PHOTO_KEY, swiperPhotoKey);
        messageBuilder.putData(SWIPE_UPDATED_AT, swipeUpdatedAt);

//        messageBuilder.setToken(this.recipientFCMToken);
//        String message = messageSource.getMessage(TITLE_CODE, new String[] {senderName}, locale);
//        messageBuilder.setNotification(Notification.builder().setTitle("Balance Notification").setBody(message).build());
        return messageBuilder.build();
    }
}
