package com.beeswork.balanceaccountservice.config;

import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.exception.BaseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class StompErrorHandler extends StompSubProtocolErrorHandler {

    private static final String ACCEPT_LANGUAGE = "accept-language";

    @Autowired
    private MessageSource messageSource;

    @Override
    protected Message<byte[]> handleInternal(@NonNull StompHeaderAccessor errorHeaderAccessor,
                                             @NonNull byte[] errorPayload,
                                             Throwable cause,
                                             StompHeaderAccessor clientHeaderAccessor) {

        String acceptLanguage = clientHeaderAccessor == null ? null : clientHeaderAccessor.getFirstNativeHeader(ACCEPT_LANGUAGE);
        Locale locale = acceptLanguage == null ? LocaleContextHolder.getLocale() : Locale.forLanguageTag(acceptLanguage);

        if (cause != null && cause.getCause() != null && cause.getCause() instanceof BaseException) {
            BaseException exception = (BaseException) cause.getCause();
            errorHeaderAccessor.setMessage(exception.getExceptionCode());
//            String exceptionMessage = messageSource.getMessage(exception.getExceptionCode(), null, locale);
//            return MessageBuilder.createMessage(exceptionMessage.getBytes(StandardCharsets.UTF_8),
//                                                errorHeaderAccessor.getMessageHeaders());
        } else {

        }

        return super.handleInternal(errorHeaderAccessor, errorPayload, cause, clientHeaderAccessor);
    }
}
