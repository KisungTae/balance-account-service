package com.beeswork.balanceaccountservice.config.websocket;

import com.beeswork.balanceaccountservice.constant.StompHeader;
import com.beeswork.balanceaccountservice.exception.BaseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompConversionException;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.util.Locale;

public class StompErrorHandler extends StompSubProtocolErrorHandler {

    @Autowired
    private MessageSource messageSource;

//  NOTE 1. if frame exceeds the size limit, then StompConversionException is thrown and is caught in handleInternal
    @Override
    protected Message<byte[]> handleInternal(@NonNull StompHeaderAccessor errorHeaderAccessor,
                                             @NonNull byte[] errorPayload,
                                             Throwable cause,
                                             StompHeaderAccessor clientHeaderAccessor) {
        System.out.println("stomperrorhandler handleInternal");
        Locale locale = StompHeader.getLocaleFromAcceptLanguageHeader(clientHeaderAccessor);
        if (cause != null && cause.getCause() != null) {
            if (cause.getCause() instanceof BaseException) {
                BaseException exception = (BaseException) cause.getCause();
                String exceptionMessage = messageSource.getMessage(exception.getExceptionCode(), null, locale);
                errorHeaderAccessor.setMessage(exceptionMessage);
                errorHeaderAccessor.addNativeHeader(StompHeader.ERROR, exception.getExceptionCode());

                if (clientHeaderAccessor != null)
                    errorHeaderAccessor.setMessageId(clientHeaderAccessor.getMessageId());
            }
        }
        return super.handleInternal(errorHeaderAccessor, errorPayload, cause, clientHeaderAccessor);
    }
}


//            return MessageBuilder.createMessage(exceptionMessage.getBytes(StandardCharsets.UTF_8),
//                                                errorHeaderAccessor.getMessageHeaders());
//            return super.handleInternal(errorHeaderAccessor, errorPayload, cause, clientHeaderAccessor);