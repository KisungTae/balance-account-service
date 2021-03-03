package com.beeswork.balanceaccountservice.dto.push;

import com.google.firebase.messaging.Message;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.UUID;


public class ClickedNotification extends AbstractNotification {

    private static final String NOTIFICATION_BODY_CODE = "clicked.notification.body";
    private static final String SWIPER_ID = "swiperId";
    private static final String REP_PHOTO_KEY = "repPhotoKey";

    private final UUID swiperId;
    private final String repPhotoKey;

    public ClickedNotification(UUID swiperId, String repPhotoKey) {
        this.swiperId = swiperId;
        this.repPhotoKey = repPhotoKey;
    }

    @Override
    public Message buildFCMMessage(Message.Builder messageBuilder, MessageSource messageSource, Locale locale) {
        setFCMNotification(messageBuilder, messageSource, locale, NOTIFICATION_BODY_CODE, null, Type.CLICKED);
        messageBuilder.setToken(recipientFCMToken);
        messageBuilder.putData(SWIPER_ID, swiperId.toString());
        messageBuilder.putData(REP_PHOTO_KEY, repPhotoKey);
        return messageBuilder.build();
    }

    @Override
    public UUID getAccountId() {
        return swiperId;
    }

}
