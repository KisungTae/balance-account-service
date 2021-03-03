package com.beeswork.balanceaccountservice.service.stomp;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dto.push.Notification;
import com.beeswork.balanceaccountservice.dto.swipe.ClickDTO;
import com.beeswork.balanceaccountservice.service.fcm.FCMService;
import com.beeswork.balanceaccountservice.vm.chat.ChatMessageVM;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class StompServiceImpl implements StompService {

    private final FCMService FCMService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final AmqpAdmin amqpAdmin;
    private final AccountDAO accountDAO;
    private static final String ACCEPT_LANGUAGE = "accept-language";

    @Autowired
    public StompServiceImpl(FCMService FCMService,
                            SimpMessagingTemplate simpMessagingTemplate,
                            AmqpAdmin amqpAdmin,
                            AccountDAO accountDAO) {
        this.FCMService = FCMService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.amqpAdmin = amqpAdmin;
        this.accountDAO = accountDAO;
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

    }

    @Override
    @Async("processExecutor")
    public void sendPushNotification(Notification notification) {

    }

    public void pushAfterClick(ClickDTO clickDTO) {

        ClickDTO pushClickDTO = new ClickDTO();
    }




}
