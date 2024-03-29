package com.beeswork.balanceaccountservice.constant;

import com.beeswork.balanceaccountservice.config.WebMvcConfig;
import org.apache.commons.lang3.LocaleUtils;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.util.MultiValueMap;

import java.util.Locale;

public class StompHeader {

    public static final String ID                            = "id";
    public static final String ACCOUNT_ID                    = "account-id";
    public static final String CHAT_ID                       = "chat-id";
    public static final String AUTO_DELETE                   = "auto-delete";
    public static final String EXCLUSIVE                     = "exclusive";
    public static final String DURABLE                       = "durable";
    public static final String SIMP_DESTINATION              = "simpDestination";
    public static final String RECIPIENT_ID                  = "recipient-id";
    public static final String QUEUE_PREFIX                  = "/queue/";
    public static final String QUEUE_SEPARATOR               = "-";
    public static final String ACCEPT_LANGUAGE               = "accept-language";
    public static final String ERROR                         = "error";
    public static final String RECEIPT                       = "receipt";
    public static final String PUSH_TYPE                     = "pushType";
    public static final String ACK                           = "ack";
    public static final String MESSAGE_ID                    = "message-id";
    public static final String PRIVATE_QUEUE_SUBSCRIPTION_ID = "0";
    public static final String DEFAULT_ACK                   = "auto";
    public static final String SUBSCRIPTION                  = "subscription";


    public static Locale getLocale(StompHeaderAccessor stompHeaderAccessor) {
        if (stompHeaderAccessor == null) {
            return WebMvcConfig.defaultLocale();
        }
        return getLocale(stompHeaderAccessor.getFirstNativeHeader(ACCEPT_LANGUAGE));
    }


    @SuppressWarnings("unchecked")
    public static Locale getLocale(MessageHeaders messageHeaders) {
        if (messageHeaders == null) {
            return WebMvcConfig.defaultLocale();
        }
        MultiValueMap<String, String> nativeHeaders = messageHeaders.get(StompHeaderAccessor.NATIVE_HEADERS, MultiValueMap.class);
        if (nativeHeaders == null) {
            return WebMvcConfig.defaultLocale();
        }
        return getLocale(nativeHeaders.getFirst(ACCEPT_LANGUAGE));
    }

    private static Locale getLocale(String localeCode) {
        if (localeCode == null) {
            return WebMvcConfig.defaultLocale();
        }
        try {
            return LocaleUtils.toLocale(localeCode);
        } catch (Exception e) {
            return WebMvcConfig.defaultLocale();
        }
    }
}
