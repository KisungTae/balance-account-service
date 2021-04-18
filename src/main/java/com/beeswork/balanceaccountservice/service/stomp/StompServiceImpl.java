package com.beeswork.balanceaccountservice.service.stomp;

import com.beeswork.balanceaccountservice.constant.StompHeader;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dto.push.Push;
import com.beeswork.balanceaccountservice.service.fcm.FCMService;
import com.beeswork.balanceaccountservice.vm.chat.ChatMessageVM;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class StompServiceImpl implements StompService {

    private final        FCMService            fcmService;
    private final        SimpMessagingTemplate simpMessagingTemplate;
    private final        AmqpAdmin             amqpAdmin;
    private final        AccountDAO            accountDAO;
    private final        MessageSource         messageSource;
    private static final String                ACCEPT_LANGUAGE = "accept-language";


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
//        String queue = chatMessageDTO.getRecipientId() + StompHeader.QUEUE_SEPARATOR + chatMessageDTO.getChatId();
//        QueueInformation queueInformation = amqpAdmin.getQueueInfo(queue);
//        if (queueInformation == null || queueInformation.getConsumerCount() <= 0) {
//            Account account = accountDAO.findById(UUID.fromString(chatMessageDTO.getAccountId()));
//            firebaseService.sendNotification(new MessageNotificationDTO(account.getName(),
//                                                                        account.getFcmToken()),
//                                             getLocaleFromMessageHeaders(messageHeaders));
//        } else simpMessagingTemplate.convertAndSend(StompHeader.QUEUE_PREFIX + queue, chatMessageDTO);
        System.out.println("simpMessagingTemplate.convertAndSend(StompHeader.SIMP_DESTINATION, chatMessageVM)");
//        simpMessagingTemplate.convertAndSend(StompHeader.SIMP_DESTINATION, chatMessageVM);
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
