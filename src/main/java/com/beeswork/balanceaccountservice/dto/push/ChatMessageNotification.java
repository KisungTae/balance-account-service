package com.beeswork.balanceaccountservice.dto.push;

import com.google.firebase.messaging.Message;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.UUID;

public class ChatMessageNotification extends AbstractNotification {

    private static final String NOTIFICATION_BODY_CODE = "message.received.notification.body";

    private final String senderName;

    public ChatMessageNotification(String senderName) {
        this.senderName = senderName;
    }

    @Override
    public Message buildFCMMessage(Message.Builder messageBuilder, MessageSource messageSource, Locale locale) {
        messageBuilder.setToken(recipientFCMToken);
        setFCMNotification(messageBuilder,
                           messageSource,
                           locale,
                           NOTIFICATION_BODY_CODE,
                           new String[]{senderName},
                           Type.CHAT_MESSAGE);
        return messageBuilder.build();
    }

    @Override
    public UUID getAccountId() {
//      TODO: return accountId
        return null;
    }
}
