package com.beeswork.balanceaccountservice.dto.push;

import com.beeswork.balanceaccountservice.util.DateUtil;
import com.google.firebase.messaging.Message;
import org.springframework.context.MessageSource;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class MatchedNotification extends AbstractNotification {

    private static final String NOTIFICATION_BODY_CODE = "matched.notification.body";
    private static final String CHAT_ID = "chatId";
    private static final String MATCHED_ID = "matchedId";
    private static final String NAME = "name";
    private static final String REP_PHOTO_KEY = "repPhotoKey";
    private static final String CREATED_AT = "createdAt";

    private final long chatId;
    private final UUID matchedId;
    private final String name;
    private final String repPhotoKey;
    private final Date createdAt;

    public MatchedNotification(long chatId,
                               UUID matchedId,
                               String name,
                               String repPhotoKey,
                               Date createdAt) {
        this.chatId = chatId;
        this.matchedId = matchedId;
        this.name = name;
        this.repPhotoKey = repPhotoKey;
        this.createdAt = createdAt;
    }

    @Override
    public Message buildFCMMessage(Message.Builder messageBuilder, MessageSource messageSource, Locale locale) {
        setFCMNotification(messageBuilder, messageSource, locale, NOTIFICATION_BODY_CODE, null, Type.MATCHED);
        messageBuilder.setToken(recipientFCMToken);
        messageBuilder.putData(CHAT_ID, String.valueOf(chatId));
        messageBuilder.putData(MATCHED_ID, matchedId.toString());
        messageBuilder.putData(NAME, name);
        messageBuilder.putData(REP_PHOTO_KEY, repPhotoKey);
        messageBuilder.putData(CREATED_AT, DateUtil.toISOString(createdAt));
        return messageBuilder.build();
    }

    @Override
    public UUID getAccountId() {
        return matchedId;
    }
}
