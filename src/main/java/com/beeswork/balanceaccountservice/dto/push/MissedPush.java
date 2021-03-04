package com.beeswork.balanceaccountservice.dto.push;


import com.google.firebase.messaging.Message;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.UUID;

public class MissedPush extends AbstractPush implements Push {

    public MissedPush() {
        super(AbstractPush.Type.MISSED);
    }

    @Override
    public Message buildFCMMessage(Message.Builder messageBuilder,
                                   MessageSource messageSource,
                                   String fcmToken,
                                   Locale locale) {
        return null;
    }

    @Override
    public UUID getAccountId() {
        return null;
    }
}
