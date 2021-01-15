package com.beeswork.balanceaccountservice.dto.account;

import com.beeswork.balanceaccountservice.dto.firebase.FirebaseNotification;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;

import java.util.Locale;


@AllArgsConstructor
public class MessageReceivedNotificationDTO extends FirebaseNotification {

    private static final String NOTIFICATION_BODY_CODE = "message.received.notification.body";

    private final String senderName;
    private final String recipientFCMToken;


    @Override
    public Message buildMessage(MessageSource messageSource, Locale locale) {
        Message.Builder messageBuilder = Message.builder();
        messageBuilder.setToken(this.recipientFCMToken);
        setNotification(messageBuilder, messageSource, locale, NOTIFICATION_BODY_CODE, new String[] {senderName}, Type.MESSAGE);
        return messageBuilder.build();
    }
}
