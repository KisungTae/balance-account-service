package com.beeswork.balanceaccountservice.dto.push;

import com.beeswork.balanceaccountservice.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.firebase.messaging.Message;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.MessageSource;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

@Getter
@Setter
public class MatchedPush extends AbstractPush {

    private static final String PUSH_BODY_CODE = "push.matched.body";
    private static final String CHAT_ID        = "chatId";
    private static final String MATCHED_ID     = "matchedId";
    private static final String NAME           = "name";
    private static final String REP_PHOTO_KEY  = "repPhotoKey";
    private static final String CREATED_AT     = "createdAt";

    private final long   chatId;
    private final UUID   matchedId;
    private final String name;
    private final String repPhotoKey;
    private final Date   createdAt;

    public MatchedPush(long chatId,
                       UUID matchedId,
                       String name,
                       String repPhotoKey,
                       Date createdAt) {
        super(Type.MATCHED);
        this.chatId = chatId;
        this.matchedId = matchedId;
        this.name = name;
        this.repPhotoKey = repPhotoKey;
        this.createdAt = createdAt;
    }

    @Override
    public Message buildFCMMessage(Message.Builder messageBuilder,
                                   MessageSource messageSource,
                                   String fcmToken,
                                   Locale locale) {
        setFCMNotification(messageBuilder, messageSource, locale, PUSH_BODY_CODE, null);
        messageBuilder.setToken(fcmToken);
        messageBuilder.putData(CHAT_ID, String.valueOf(chatId));
        messageBuilder.putData(MATCHED_ID, matchedId.toString());
        messageBuilder.putData(NAME, name);
        messageBuilder.putData(REP_PHOTO_KEY, repPhotoKey);
        messageBuilder.putData(CREATED_AT, DateUtil.toISOString(createdAt));
        return messageBuilder.build();
    }

    @Override
    @JsonIgnore
    public UUID getAccountId() {
        return matchedId;
    }
}
