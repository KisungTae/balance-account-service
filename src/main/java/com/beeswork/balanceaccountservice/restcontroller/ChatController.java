package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.vm.chat.ChatMessageVM;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final AmqpAdmin             amqpAdmin;


    @Autowired
    public ChatController(SimpMessagingTemplate simpMessagingTemplate,
                          AmqpAdmin amqpAdmin) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.amqpAdmin = amqpAdmin;
    }

    @MessageMapping("/chat/send")
    public void send(@Payload ChatMessageVM chatMessageVM, MessageHeaders messageHeaders) {

        MultiValueMap<String, String>
                multiValueMap = messageHeaders.get(StompHeaderAccessor.NATIVE_HEADERS, MultiValueMap.class);

        String accountId = multiValueMap.getFirst("account-id");
        String chatId = multiValueMap.getFirst("chat-id");
        String recipientId = multiValueMap.getFirst("recipient-id");
        String messageId = multiValueMap.getFirst("message-id");


        QueueInformation queueInformation = amqpAdmin.getQueueInfo(recipientId + "-" + chatId);
        if (queueInformation == null)
            System.out.println("queueInformation is null");
        else {
            System.out.println("consumer count: " + queueInformation.getConsumerCount());
        }

        Map<String, Object> headers = new HashMap<>();
        headers.put("auto-delete", true);
        simpMessagingTemplate.convertAndSend("/queue/" + recipientId + "-" + chatId, chatMessageVM, headers);
    }
}


// maximum topic

// no subscriber channel, have to be handled manually,

// how @subscribe works

// how to check if there is subscriber discconected
