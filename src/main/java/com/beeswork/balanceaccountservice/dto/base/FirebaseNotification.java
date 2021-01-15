package com.beeswork.balanceaccountservice.dto.base;

import com.google.firebase.messaging.Message;
import org.springframework.context.MessageSource;

import java.util.Locale;

public interface FirebaseNotification {
    Message buildMessage(MessageSource messageSource, Locale locale);
}
