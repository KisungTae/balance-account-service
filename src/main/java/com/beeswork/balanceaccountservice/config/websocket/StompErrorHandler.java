package com.beeswork.balanceaccountservice.config.websocket;

import com.beeswork.balanceaccountservice.constant.StompHeader;
import com.beeswork.balanceaccountservice.exception.BaseException;

import com.beeswork.balanceaccountservice.exception.InternalServerException;
import com.beeswork.balanceaccountservice.exception.match.MatchNotFoundException;
import io.jsonwebtoken.ExpiredJwtException;
import io.micrometer.core.lang.NonNullApi;
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
        Locale locale = StompHeader.getLocaleFromAcceptLanguageHeader(clientHeaderAccessor);
        if (cause != null && cause.getCause() != null) {
            BaseException exception;
            if (cause.getCause() instanceof BaseException) {
                exception = (BaseException) cause.getCause();
            } else {
                exception = new InternalServerException();
            }
            String exceptionMessage = messageSource.getMessage(exception.getExceptionCode(), null, locale);
            errorHeaderAccessor.addNativeHeader(StompHeader.ERROR, exception.getExceptionCode());
            errorHeaderAccessor.setMessage(exceptionMessage);
        }
        return super.handleInternal(errorHeaderAccessor, errorPayload, cause, clientHeaderAccessor);
    }
}