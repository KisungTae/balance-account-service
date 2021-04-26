package com.beeswork.balanceaccountservice.service.stomp;

import com.beeswork.balanceaccountservice.constant.PushType;
import com.beeswork.balanceaccountservice.constant.StompHeader;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dto.push.Push;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.exception.swipe.SwipedNotFoundException;
import com.beeswork.balanceaccountservice.service.fcm.FCMService;
import com.beeswork.balanceaccountservice.vm.chat.ChatMessageVM;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StompServiceImpl implements StompService {

    private final FCMService fcmService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final AmqpAdmin amqpAdmin;
    private final AccountDAO accountDAO;
    private final MessageSource messageSource;
    private static final String ACCEPT_LANGUAGE = "accept-language";


    @Autowired
    public StompServiceImpl(FCMService fcmService,
                            SimpMessagingTemplate simpMessagingTemplate,
                            AmqpAdmin amqpAdmin,
                            AccountDAO accountDAO,
                            MessageSource messageSource) {
        this.fcmService = fcmService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.amqpAdmin = amqpAdmin;
        this.accountDAO = accountDAO;
        this.messageSource = messageSource;
    }

    //  NOTE 1. convertAndSend to not existing queue, it creates a new queue under the queue desitination
    @Override
    public void sendChatMessage(ChatMessageVM chatMessageVM, MessageHeaders messageHeaders) {
        if (chatMessageVM.getId() == null) return;

        UUID recipientId = chatMessageVM.getRecipientId();
        if (recipientId == null) throw new SwipedNotFoundException();
        QueueInformation queueInformation = amqpAdmin.getQueueInfo(recipientId.toString());

        if (queueInformation == null || queueInformation.getConsumerCount() <= 0) {
            System.out.println("queue not found in rabbitmq");
//            Account account = accountDAO.findById(UUID.fromString(chatMessageDTO.getAccountId()));
//            firebaseService.sendNotification(new MessageNotificationDTO(account.getName(),
//                                                                        account.getFcmToken()),
//                                             getLocaleFromMessageHeaders(messageHeaders));
        } else {
            Map<String, Object> headers = new HashMap<>();
            headers.put(StompHeader.PUSH_TYPE, PushType.CHAT_MESSAGE);
            headers.put(StompHeader.AUTO_DELETE, true);
            headers.put(StompHeader.EXCLUSIVE, false);
            headers.put(StompHeader.DURABLE, true);
            simpMessagingTemplate.convertAndSend(StompHeader.QUEUE_PREFIX + recipientId.toString(), chatMessageVM, new MessageHeaders(headers));
        }


//        String queue = chatMessageDTO.getRecipientId() + StompHeader.QUEUE_SEPARATOR + chatMessageDTO.getChatId();
//        QueueInformation queueInformation = amqpAdmin.getQueueInfo(queue);
//        if (queueInformation == null || queueInformation.getConsumerCount() <= 0) {
//            Account account = accountDAO.findById(UUID.fromString(chatMessageDTO.getAccountId()));
//            firebaseService.sendNotification(new MessageNotificationDTO(account.getName(),
//                                                                        account.getFcmToken()),
//                                             getLocaleFromMessageHeaders(messageHeaders));
//        } else simpMessagingTemplate.convertAndSend(StompHeader.QUEUE_PREFIX + queue, chatMessageDTO);
//        messageHeaders.put("test-header", "");
//        Map<String, Object> headers = new HashMap<>();
//        headers.put(StompHeader.PUSH_TYPE, PushType.CHAT_MESSAGE);
//        headers.put("message-id", chatMessageVM.getId());
//
//        simpMessagingTemplate.convertAndSend("/queue/136d4f5e-469c-4fc0-9d7d-d04c895bf99d", chatMessageVM);
    }

    @Override
    @Async("processExecutor")
    public void sendPush(Push push, Locale locale) {
        if (push == null) return;
        String queue = push.getAccountId().toString();
        if (queueExists(queue)) simpMessagingTemplate.convertAndSend(queue, push);
        else fcmService.sendPush(push, locale);
    }

    private boolean queueExists(String queue) {
        QueueInformation queueInformation = amqpAdmin.getQueueInfo(queue);
        return queueInformation != null && queueInformation.getConsumerCount() > 0;
    }

}
