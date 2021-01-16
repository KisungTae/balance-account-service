package com.beeswork.balanceaccountservice.dto.firebase;

import com.beeswork.balanceaccountservice.dto.firebase.AbstractFirebaseNotification;
import com.google.firebase.messaging.Message;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;

import java.util.Locale;


public class ClickedNotificationDTO extends AbstractFirebaseNotification {

    private static final String NOTIFICATION_BODY_CODE = "clicked.notification.body";
    private static final String SWIPER_NAME            = "swiperName";
    private static final String SWIPER_PHOTO_KEY       = "swiperPhotoKey";

    private final String swiperName;
    private final String swiperPhotoKey;

    public ClickedNotificationDTO(String swiperName, String swiperPhotoKey, String recipientFCMToken) {
        super(recipientFCMToken);
        this.swiperName = swiperName;
        this.swiperPhotoKey = swiperPhotoKey;
    }

    @Override
    public Message buildMessage(Message.Builder messageBuilder, MessageSource messageSource, Locale locale) {
        setNotification(messageBuilder, messageSource, locale, NOTIFICATION_BODY_CODE, null, Type.CLICKED);
        messageBuilder.setToken(recipientFCMToken);
        messageBuilder.putData(SWIPER_NAME, swiperName);
        messageBuilder.putData(SWIPER_PHOTO_KEY, swiperPhotoKey);
        return messageBuilder.build();
    }

}
