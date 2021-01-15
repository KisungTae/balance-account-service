package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.config.StompChannelInterceptor;
import com.beeswork.balanceaccountservice.dto.account.MessageReceivedNotificationDTO;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.service.account.AccountService;
import com.beeswork.balanceaccountservice.service.chat.ChatService;
import com.beeswork.balanceaccountservice.service.firebase.FirebaseService;
import com.beeswork.balanceaccountservice.vm.chat.ChatMessageVM;
import org.apache.commons.lang3.LocaleUtils;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Locale;

@RestController
public class ChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final AmqpAdmin amqpAdmin;
    private final ChatService chatService;
    private final FirebaseService firebaseService;
    private static final String ACCEPT_LANGUAGE = "accept-language";

    @Autowired
    public ChatController(SimpMessagingTemplate simpMessagingTemplate, AmqpAdmin amqpAdmin, ChatService chatService, FirebaseService firebaseService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.amqpAdmin = amqpAdmin;
        this.chatService = chatService;
        this.firebaseService = firebaseService;
    }

    @MessageMapping("/chat/send")
    public void send(@Payload ChatMessageVM chatMessageVM, MessageHeaders messageHeaders) {
        QueueInformation queueInformation = amqpAdmin.getQueueInfo(StompChannelInterceptor.queueName(chatMessageVM.getRecipientId(),
                                                                                                     chatMessageVM.getChatId()));
        if (queueInformation == null || queueInformation.getConsumerCount() <= 0) {
            MultiValueMap<String, String> nativeHeaders = messageHeaders.get(StompHeaderAccessor.NATIVE_HEADERS,
                                                                             MultiValueMap.class);
            MessageReceivedNotificationDTO messageReceivedNotificationDTO =
                    chatService.getMessageReceivedNotification(chatMessageVM.getAccountId());
            if (messageReceivedNotificationDTO != null) {
                Locale locale = LocaleUtils.toLocale(nativeHeaders ==
                                                     null ? "" : nativeHeaders.getFirst(ACCEPT_LANGUAGE));
                firebaseService.sendNotification(messageReceivedNotificationDTO, locale);
            }
        } else
            simpMessagingTemplate.convertAndSend(StompChannelInterceptor.queueName(chatMessageVM.getRecipientId(),
                                                                                   chatMessageVM.getChatId()),
                                                 chatMessageVM);
    }
}


// maximum topic

// no subscriber channel, have to be handled manually,

// how @subscribe works

// how to check if there is subscriber discconected
