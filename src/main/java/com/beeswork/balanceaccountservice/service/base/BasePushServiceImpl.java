package com.beeswork.balanceaccountservice.service.base;

import com.beeswork.balanceaccountservice.constant.PushType;
import org.apache.tomcat.jni.Local;
import org.springframework.context.MessageSource;

import java.util.Locale;

public abstract class BasePushServiceImpl {

    private final MessageSource messageSource;
    private static final String TITLE_CLICKED = "push.title.clicked";
    private static final String TITLE_MATCHED = "push.title.matched";
    private static final String TITLE_CHAT_MESSAGE = "push.title.chat.message";
    private static final String BODY_CLICKED = "push.body.clicked";
    private static final String BODY_MATCHED = "push.body.matched";
    private static final String BODY_CHAT_MESSAGE = "push.body.chat.message";

    protected BasePushServiceImpl(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    protected String getTitle(PushType pushType, Locale locale) {
        switch (pushType) {
            case CLICKED: return messageSource.getMessage(TITLE_CLICKED, null, locale);
            case MATCHED: return messageSource.getMessage(TITLE_MATCHED, null, locale);
            case CHAT_MESSAGE: return messageSource.getMessage(TITLE_CHAT_MESSAGE, null, locale);
            default: return "";
        }
    }

    protected String getBody(PushType pushType, String[] arguments, Locale locale) {
        switch (pushType) {
            case CLICKED: return messageSource.getMessage(BODY_CLICKED, arguments, locale);
            case MATCHED: return messageSource.getMessage(BODY_MATCHED, arguments, locale);
            case CHAT_MESSAGE: return messageSource.getMessage(BODY_CHAT_MESSAGE, arguments, locale);
            default: return "";
        }
    }
}
