package com.beeswork.balanceaccountservice.dto.push;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.firebase.messaging.Message;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.MessageSource;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class ChatMessagePush extends AbstractPush {

    private static final String PUSH_BODY_CODE = "push.chat.message.body";

    private String senderName;

    public ChatMessagePush(String senderName) {
        super(Type.CHAT_MESSAGE);
        this.senderName = senderName;
    }

    @Override
    public Message buildFCMMessage(Message.Builder messageBuilder,
                                   MessageSource messageSource,
                                   String fcmToken,
                                   Locale locale) {
        messageBuilder.setToken(fcmToken);
        setFCMNotification(messageBuilder, messageSource, locale, PUSH_BODY_CODE, new String[]{senderName});
        return messageBuilder.build();
    }

    @Override
    @JsonIgnore
    public UUID getAccountId() {
//      TODO: return accountId
        return null;
    }
}
