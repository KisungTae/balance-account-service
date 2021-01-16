package com.beeswork.balanceaccountservice.dto.firebase;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.context.MessageSource;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Locale;


public abstract class AbstractFirebaseNotification implements FirebaseNotification {

    protected final String recipientFCMToken;

    protected AbstractFirebaseNotification(String recipientFCMToken) {
        this.recipientFCMToken = recipientFCMToken;
    }


    public enum Type {
        CLICKED,
        MATCHED,
        MESSAGE
    }

    protected static final String NOTIFICATION_TITLE_CODE = "notification.title";
    protected static final String NOTIFICATION_TYPE = "notificationType";

    public abstract Message buildMessage(Message.Builder messageBuilder, MessageSource messageSource, Locale locale);

    public boolean validateFCMToken() {
        return recipientFCMToken != null;
    }

    protected void setNotification(Message.Builder messageBuilder,
                                   MessageSource messageSource,
                                   Locale locale,
                                   String notificationBodyCode,
                                   String[] notificationBodyArguments,
                                   Type notificationType) {
        String body = messageSource.getMessage(notificationBodyCode, notificationBodyArguments, locale);
        String title = messageSource.getMessage(NOTIFICATION_TITLE_CODE, null, locale);
        messageBuilder.setNotification(Notification.builder().setTitle(title).setBody(body).build());
        messageBuilder.putData(NOTIFICATION_TYPE, notificationType.toString());
    }
}
