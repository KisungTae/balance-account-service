package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.service.chat.ChatService;
import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.service.stomp.StompService;
import org.apache.commons.lang3.LocaleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
public class ChatController {

    private final StompService stompService;
    private static final String      ACCEPT_LANGUAGE = "accept-language";

    @Autowired
    public ChatController(StompService stompService) {
        this.stompService = stompService;
    }

    @MessageMapping("/chat/send")
    public void send(@Payload ChatMessageDTO chatMessageDTO, MessageHeaders messageHeaders) {
        stompService.send(chatMessageDTO, getLocaleFromMessageHeaders(messageHeaders));
    }

    @SuppressWarnings("unchecked")
    private Locale getLocaleFromMessageHeaders(MessageHeaders messageHeaders) {
        MultiValueMap<String, String> nativeHeaders = messageHeaders.get(StompHeaderAccessor.NATIVE_HEADERS,
                                                                         MultiValueMap.class);
        return LocaleUtils.toLocale(nativeHeaders == null ? "" : nativeHeaders.getFirst(ACCEPT_LANGUAGE));
    }
}


// maximum topic

// no subscriber channel, have to be handled manually,

// how @subscribe works

// how to check if there is subscriber discconected
