package com.beeswork.balanceaccountservice.service.stomp;

import com.beeswork.balanceaccountservice.constant.PushType;
import com.beeswork.balanceaccountservice.constant.StompHeader;
import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.dto.common.Pushable;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.service.push.PushService;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StompServiceImpl implements StompService {

    private final PushService pushService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final AmqpAdmin amqpAdmin;

    @Autowired
    public StompServiceImpl(PushService pushService,
                            SimpMessagingTemplate simpMessagingTemplate,
                            AmqpAdmin amqpAdmin) {
        this.pushService = pushService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.amqpAdmin = amqpAdmin;
    }

    //  NOTE 1. convertAndSend to not existing queue, it creates a new queue under the queue desitination
    @Override
    public void pushChatMessage(ChatMessageDTO chatMessageDTO, Locale locale) {
        if (chatMessageDTO == null || chatMessageDTO.getId() == null) {
            return;
        }
        String queue = getQueue(chatMessageDTO.getRecipientId());
        if (queue == null) {
            pushService.pushChatMessage(chatMessageDTO, locale);
        } else {
            MessageHeaders outHeaders = sendingHeaders(chatMessageDTO.getPushType());
            chatMessageDTO.setRecipientId(null);
            chatMessageDTO.setAccountId(null);
            simpMessagingTemplate.convertAndSend(queue, chatMessageDTO, outHeaders);
        }
    }

    @Override
    @Async("processExecutor")
    public void push(Pushable pushable, Locale locale) {
        if (pushable == null) {
            return;
        }
        String queue = getQueue(pushable.getRecipientId());
        if (queue == null) {
            pushService.push(pushable, locale);
        } else {
            MessageHeaders outHeaders = sendingHeaders(pushable.getPushType());
            simpMessagingTemplate.convertAndSend(queue, pushable, outHeaders);
        }
    }

    private String getQueue(UUID queue) {
        if (queue == null) {
            return null;
        }
        QueueInformation queueInformation = amqpAdmin.getQueueInfo(queue.toString());
        if (queueInformation == null || queueInformation.getConsumerCount() <= 0) {
            return null;
        }
        return StompHeader.QUEUE_PREFIX + queue.toString();
    }

    private MessageHeaders sendingHeaders(PushType pushType) {
        Map<String, Object> headers = new HashMap<>();
        headers.put(StompHeader.AUTO_DELETE, true);
        headers.put(StompHeader.EXCLUSIVE, false);
        headers.put(StompHeader.DURABLE, true);
        headers.put(StompHeader.PUSH_TYPE, pushType);
//        headers.put(StompHeader.SUBSCRIPTION, StompHeader.PRIVATE_QUEUE_SUBSCRIPTION_ID);
        return new MessageHeaders(headers);
    }
}
