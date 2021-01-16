package com.beeswork.balanceaccountservice.dto.firebase;

import com.google.firebase.messaging.Message;
import org.springframework.context.MessageSource;

import java.util.Locale;

public class MatchedNotificationDTO extends AbstractFirebaseNotification {

    private static final String NOTIFICATION_BODY_CODE = "matched.notification.body";
    private static final String MATCHER_NAME           = "matcherName";
    private static final String MATCHER_PHOTO_KEY      = "matcherPhotoKey";

    private final String matcherName;
    private final String matcherPhotoKey;

    public MatchedNotificationDTO(String recipientFCMToken, String matcherName, String matcherPhotoKey) {
        super(recipientFCMToken);
        this.matcherName = matcherName;
        this.matcherPhotoKey = matcherPhotoKey;
    }

    @Override
    public Message buildMessage(Message.Builder messageBuilder, MessageSource messageSource, Locale locale) {
        setNotification(messageBuilder, messageSource, locale, NOTIFICATION_BODY_CODE, null, Type.CLICKED);
        messageBuilder.setToken(recipientFCMToken);
        messageBuilder.putData(MATCHER_NAME, matcherName);
        messageBuilder.putData(MATCHER_PHOTO_KEY, matcherPhotoKey);
        return messageBuilder.build();
    }
}
