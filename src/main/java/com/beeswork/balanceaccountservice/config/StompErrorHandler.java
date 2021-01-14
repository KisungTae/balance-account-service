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
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        System.out.println("handleClientMessageProcessingError");
        return super.handleClientMessageProcessingError(clientMessage, ex);
    }



    @Override
    public Message<byte[]> handleErrorMessageToClient(Message<byte[]> errorMessage) {
        System.out.println("handleErrorMessageToClient");
        return super.handleErrorMessageToClient(errorMessage);
    }

    @Override
    protected Message<byte[]> handleInternal(@NonNull StompHeaderAccessor errorHeaderAccessor,
                                             @NonNull byte[] errorPayload,
                                             Throwable cause,
                                             StompHeaderAccessor clientHeaderAccessor) {

        System.out.println("stomp handleInternal!!!!!!!!!!!!");


        String acceptLanguage = clientHeaderAccessor == null ? null : clientHeaderAccessor.getFirstNativeHeader(ACCEPT_LANGUAGE);
        Locale locale = acceptLanguage == null ? LocaleContextHolder.getLocale() : Locale.forLanguageTag(acceptLanguage);

        if (cause != null && cause.getCause() != null && cause.getCause() instanceof BaseException) {
            BaseException exception = (BaseException) cause.getCause();
            String exceptionMessage = messageSource.getMessage(exception.getExceptionCode(), null, locale);
            errorHeaderAccessor.setMessage(exceptionMessage);
            errorHeaderAccessor.addNativeHeader("exceptionCode", exception.getExceptionCode());

//            return MessageBuilder.createMessage(exceptionMessage.getBytes(StandardCharsets.UTF_8),
//                                                errorHeaderAccessor.getMessageHeaders());
//            return super.handleInternal(errorHeaderAccessor, errorPayload, cause, clientHeaderAccessor);
        } else {

        }

        return super.handleInternal(errorHeaderAccessor, errorPayload, cause, clientHeaderAccessor);
    }
}
