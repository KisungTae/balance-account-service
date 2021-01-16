package com.beeswork.balanceaccountservice.dto.firebase;

import com.google.firebase.messaging.Message;
import org.springframework.context.MessageSource;

import java.util.Locale;

public interface FirebaseNotification {

    Message buildMessage(Message.Builder messageBuilder, MessageSource messageSource, Locale locale);
    boolean validateFCMToken();
}
