package com.beeswork.balanceaccountservice.dto.push;

import com.google.firebase.messaging.Message;
import org.springframework.context.MessageSource;

import java.util.Locale;


public abstract class AbstractNotification implements Notification {

    public enum Type {
        CLICKED,
        MATCHED,
        CHAT_MESSAGE
    }

    protected static final String NOTIFICATION_TITLE_CODE = "notification.title";
    protected static final String NOTIFICATION_TYPE = "notificationType";
    protected String recipientFCMToken;


    public abstract Message buildFCMMessage(Message.Builder messageBuilder, MessageSource messageSource, Locale locale);

    protected void setFCMNotification(Message.Builder messageBuilder,
                                      MessageSource messageSource,
                                      Locale locale,
                                      String notificationBodyCode,
                                      String[] notificationBodyArguments,
                                      Type notificationType) {
        String body = messageSource.getMessage(notificationBodyCode, notificationBodyArguments, locale);
        String title = messageSource.getMessage(NOTIFICATION_TITLE_CODE, null, locale);
        messageBuilder.setNotification(com.google.firebase.messaging.Notification.builder().setTitle(title).setBody(body).build());
        messageBuilder.putData(NOTIFICATION_TYPE, notificationType.toString());
    }
}
