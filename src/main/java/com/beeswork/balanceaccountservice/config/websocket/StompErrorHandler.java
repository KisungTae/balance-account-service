package com.beeswork.balanceaccountservice.config.websocket;

import com.beeswork.balanceaccountservice.constant.StompHeader;
import com.beeswork.balanceaccountservice.exception.BaseException;

import com.beeswork.balanceaccountservice.exception.InternalServerException;
import com.beeswork.balanceaccountservice.exception.jwt.ExpiredJWTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
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
        Locale locale = StompHeader.getLocale(clientHeaderAccessor);
        if (cause != null && cause.getCause() != null) {
            String exceptionCode = "";
            if (cause.getCause() instanceof BaseException) {
                exceptionCode = ((BaseException) cause.getCause()).getExceptionCode();
            } else if (cause.getCause() instanceof ExpiredJWTException) {
                exceptionCode = ((ExpiredJWTException) cause.getCause()).getExceptionCode();
            } else {
                exceptionCode = InternalServerException.CODE;
            }
            String exceptionMessage = messageSource.getMessage(exceptionCode, null, locale);
            if (clientHeaderAccessor.getReceipt() != null) {
                errorHeaderAccessor.setReceiptId(clientHeaderAccessor.getReceipt());
            }
            errorHeaderAccessor.addNativeHeader(StompHeader.ERROR, exceptionCode);
            errorHeaderAccessor.setMessage(exceptionMessage);
        }
        return super.handleInternal(errorHeaderAccessor, errorPayload, cause, clientHeaderAccessor);
    }
}