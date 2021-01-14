package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.service.chat.ChatService;
import com.beeswork.balanceaccountservice.vm.chat.ChatMessageVM;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final AmqpAdmin amqpAdmin;
    private final ChatService chatService;

    @Autowired
    public ChatController(SimpMessagingTemplate simpMessagingTemplate,
                          AmqpAdmin amqpAdmin, ChatService chatService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.amqpAdmin = amqpAdmin;
        this.chatService = chatService;
    }

    @MessageMapping("/chat/send")
    public void send(@Payload ChatMessageVM chatMessageVM, MessageHeaders messageHeaders) {

//        MultiValueMap<String, String> nativeHeaders = messageHeaders.get(StompHeaderAccessor.NATIVE_HEADERS,
//                                                                   MultiValueMap.class);
//
//        if (nativeHeaders != null) {
//            Map<String, Object> messageHeadersToSend = new HashMap<>();
//            messageHeaders.put(StompHeader.CHAT_ID, nativeHeaders.getFirst(StompHeader.CHAT_ID));
//
//            chatMessageVM.setChatId(nativeHeaders.getFirst(StompHeader.CHAT_ID));
//            chatMessageVM.setRecipientId(nativeHeaders.getFirst(StompHeader.MATCHED_ID));
//            chatMessageVM.setSenderId(nativeHeaders.getFirst(StompHeader.ACCOUNT_ID));
//            String queue = StompChannelInterceptor.queueName(chatMessageVM.getRecipientId(), chatMessageVM.getChatId());
//            QueueInformation queueInformation = amqpAdmin.getQueueInfo(queue);
//
//            if (queueInformation == null)
//                System.out.println("queueInformation is null");
//            else {
//                System.out.println("consumer count: " + queueInformation.getConsumerCount());
//
//                simpMessagingTemplate.convertAndSend(queue, chatMessageVM, messageHeadersToSend);
//            }
//
//
//        }

        System.out.println("send before throwing exception");
        throw new BadRequestException();


    }
}


// maximum topic

// no subscriber channel, have to be handled manually,

// how @subscribe works

// how to check if there is subscriber discconected
