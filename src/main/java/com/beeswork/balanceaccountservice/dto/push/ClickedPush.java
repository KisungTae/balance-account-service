package com.beeswork.balanceaccountservice.dto.push;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.firebase.messaging.Message;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.UUID;

@Getter
@Setter
public class ClickedPush extends AbstractPush {

    private static final String PUSH_BODY_CODE = "push.clicked.body";
    private static final String SWIPER_ID      = "swiperId";
    private static final String REP_PHOTO_KEY  = "repPhotoKey";

    private final UUID   swiperId;
    private final String repPhotoKey;

    public ClickedPush(UUID swiperId, String repPhotoKey) {
        super(Type.CLICKED);
        this.swiperId = swiperId;
        this.repPhotoKey = repPhotoKey;
    }

    @Override
    public Message buildFCMMessage(Message.Builder messageBuilder,
                                   MessageSource messageSource,
                                   String fcmToken,
                                   Locale locale) {
        messageBuilder.setToken(fcmToken);
        setFCMNotification(messageBuilder, messageSource, locale, PUSH_BODY_CODE, null);
        messageBuilder.putData(SWIPER_ID, swiperId.toString());
        messageBuilder.putData(REP_PHOTO_KEY, repPhotoKey);
        return messageBuilder.build();
    }

    @Override
    @JsonIgnore
    public UUID getAccountId() {
        return swiperId;
    }

}
