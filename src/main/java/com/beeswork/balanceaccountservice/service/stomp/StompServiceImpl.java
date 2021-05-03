package com.beeswork.balanceaccountservice.service.stomp;

import com.beeswork.balanceaccountservice.constant.PushType;
import com.beeswork.balanceaccountservice.constant.StompHeader;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.dto.push.Push;
import com.beeswork.balanceaccountservice.exception.stomp.QueueNotFoundException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipedNotFoundException;
import com.beeswork.balanceaccountservice.service.push.PushService;
import com.beeswork.balanceaccountservice.vm.chat.ChatMessageVM;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.messaging.Message;
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
    @Async("processExecutor")
    public void sendChatMessage(ChatMessageDTO chatMessageDTO, MessageHeaders messageHeaders) {
        if (chatMessageDTO.getId() == null) return;
        String queue = getQueue(chatMessageDTO.getRecipientId());
        if (queue != null) {
            MessageHeaders outHeaders = sendingHeaders(PushType.CHAT_MESSAGE, chatMessageDTO.getId().toString());
            simpMessagingTemplate.convertAndSend(queue, chatMessageDTO, outHeaders);
        } else pushService.pushChatMessage(chatMessageDTO, StompHeader.getLocaleFromAcceptLanguageHeader(messageHeaders));

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

    private MessageHeaders sendingHeaders(PushType pushType, String messageId) {
        Map<String, Object> headers = new HashMap<>();
        headers.put(StompHeader.AUTO_DELETE, true);
        headers.put(StompHeader.EXCLUSIVE, false);
        headers.put(StompHeader.DURABLE, true);
        headers.put(StompHeader.PUSH_TYPE, pushType);
        headers.put(StompHeader.MESSAGE_ID, messageId);
        headers.put(StompHeader.SUBSCRIPTION, StompHeader.PRIVATE_QUEUE_SUBSCRIPTION_ID);
        return new MessageHeaders(headers);
    }

    @Override
    @Async("processExecutor")
    public void sendMatch(MatchDTO matchDTO) {
        if (matchDTO == null) return;
        String queue = getQueue(matchDTO.getMatcherId());
        if (queue != null) {
            MessageHeaders outHeaders = sendingHeaders(PushType.MATCH, matchDTO.getChatId().toString());
            simpMessagingTemplate.convertAndSend(queue, matchDTO, outHeaders);
        } else pushService.pushMatch(matchDTO);
    }

    @Override
    @Async("processExecutor")
    public void sendPush(Push push, Locale locale) {
//        if (push == null) return;
//        String queue = push.getAccountId().toString();
//        if (queueExists(queue)) simpMessagingTemplate.convertAndSend(queue, push);
//        else fcmService.sendPush(push, locale);
    }

    private String getQueue(UUID queue) {
        if (queue == null) throw new QueueNotFoundException();
        QueueInformation queueInformation = amqpAdmin.getQueueInfo(queue.toString());
        if (queueInformation == null || queueInformation.getConsumerCount() <= 0) return null;
        return StompHeader.QUEUE_PREFIX + queue.toString();
    }

}
