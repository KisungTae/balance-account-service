package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.exception.BadRequestException;
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

import java.util.Date;
import java.util.Locale;

@RestController
public class ChatController {

    private final StompService stompService;

    @Autowired
    public ChatController(StompService stompService) {
        this.stompService = stompService;
    }

    @MessageMapping("/chat/send")
    public void send(@Payload ChatMessageDTO chatMessageDTO, MessageHeaders messageHeaders) {
        System.out.println("send!!!!!!!!!!!!!!!!!!!!!!!!!");
        stompService.send(chatMessageDTO, messageHeaders);
    }

}


// maximum topic

// no subscriber channel, have to be handled manually,

// how @subscribe works

// how to check if there is subscriber discconected
