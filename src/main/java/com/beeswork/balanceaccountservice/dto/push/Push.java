package com.beeswork.balanceaccountservice.dto.push;

import com.google.firebase.messaging.Message;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.UUID;

public interface Push {

    Message buildFCMMessage(Message.Builder messageBuilder,
                            MessageSource messageSource,
                            String fcmToken,
                            Locale locale);

    UUID getAccountId();
}