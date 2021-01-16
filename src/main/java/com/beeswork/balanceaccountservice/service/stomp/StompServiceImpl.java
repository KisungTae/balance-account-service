package com.beeswork.balanceaccountservice.service.stomp;

import com.beeswork.balanceaccountservice.config.StompChannelInterceptor;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.dto.firebase.MessageReceivedNotificationDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.service.firebase.FirebaseService;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;

@Service
public class StompServiceImpl implements StompService {

    private final FirebaseService       firebaseService;
    private final     SimpMessagingTemplate simpMessagingTemplate;
    private final     AmqpAdmin             amqpAdmin;
    private final AccountDAO accountDAO;

    @Autowired
    public StompServiceImpl(FirebaseService firebaseService,
                            SimpMessagingTemplate simpMessagingTemplate,
                            AmqpAdmin amqpAdmin, AccountDAO accountDAO) {
        this.firebaseService = firebaseService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.amqpAdmin = amqpAdmin;
        this.accountDAO = accountDAO;
    }

    @Override
    public void send(ChatMessageDTO chatMessageDTO, Locale locale) {
        String queue = StompChannelInterceptor.queueName(chatMessageDTO.getRecipientId(), chatMessageDTO.getChatId());
        QueueInformation queueInformation = amqpAdmin.getQueueInfo(queue);

        if (queueInformation == null || queueInformation.getConsumerCount() <= 0) {
            Account account = accountDAO.findById(UUID.fromString(chatMessageDTO.getAccountId()));
            firebaseService.sendNotification(new MessageReceivedNotificationDTO(account.getName(),
                                                                                account.getFcmToken()),
                                             locale);
        } else simpMessagingTemplate.convertAndSend(queue, chatMessageDTO);
    }
}
