package com.beeswork.balanceaccountservice.dto.firebase;

import com.beeswork.balanceaccountservice.dto.firebase.AbstractFirebaseNotification;
import com.google.firebase.messaging.Message;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;

import java.util.Locale;

public class MessageNotificationDTO extends AbstractFirebaseNotification {

    private static final String NOTIFICATION_BODY_CODE = "message.received.notification.body";

    private final String senderName;

    public MessageNotificationDTO(String senderName, String recipientFCMToken) {
        super(recipientFCMToken);
        this.senderName = senderName;
    }

    @Override
    public Message buildMessage(Message.Builder messageBuilder, MessageSource messageSource, Locale locale) {
        messageBuilder.setToken(recipientFCMToken);
        setNotification(messageBuilder,
                        messageSource,
                        locale,
                        NOTIFICATION_BODY_CODE,
                        new String[]{senderName},
                        Type.MESSAGE);
        return messageBuilder.build();
    }
}
