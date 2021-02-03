package com.beeswork.balanceaccountservice.service.stomp;

import com.beeswork.balanceaccountservice.config.WebConfig;
import com.beeswork.balanceaccountservice.constant.StompHeader;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.dto.firebase.MessageNotificationDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.service.firebase.FirebaseService;
import com.beeswork.balanceaccountservice.vm.chat.ChatMessageVM;
import org.apache.commons.lang3.LocaleUtils;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.Locale;
import java.util.UUID;

@Service
public class StompServiceImpl implements StompService {

    private final        FirebaseService       firebaseService;
    private final        SimpMessagingTemplate simpMessagingTemplate;
    private final        AmqpAdmin             amqpAdmin;
    private final        AccountDAO            accountDAO;
    private static final String                ACCEPT_LANGUAGE = "accept-language";

    @Autowired
    public StompServiceImpl(FirebaseService firebaseService,
                            SimpMessagingTemplate simpMessagingTemplate,
                            AmqpAdmin amqpAdmin,
                            AccountDAO accountDAO) {
        this.firebaseService = firebaseService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.amqpAdmin = amqpAdmin;
        this.accountDAO = accountDAO;
    }

//  NOTE 1. convertAndSend to not existing queue, it creates a new queue under the queue desitination
    @Override
    public void send(ChatMessageVM chatMessageVM, MessageHeaders messageHeaders) {
//        String queue = chatMessageDTO.getRecipientId() + StompHeader.QUEUE_SEPARATOR + chatMessageDTO.getChatId();
//        QueueInformation queueInformation = amqpAdmin.getQueueInfo(queue);
//        if (queueInformation == null || queueInformation.getConsumerCount() <= 0) {
//            Account account = accountDAO.findById(UUID.fromString(chatMessageDTO.getAccountId()));
//            firebaseService.sendNotification(new MessageNotificationDTO(account.getName(),
//                                                                        account.getFcmToken()),
//                                             getLocaleFromMessageHeaders(messageHeaders));
//        } else simpMessagingTemplate.convertAndSend(StompHeader.QUEUE_PREFIX + queue, chatMessageDTO);
    }
}
