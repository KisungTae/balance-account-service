package com.beeswork.balanceaccountservice.service.stomp;

import com.beeswork.balanceaccountservice.dto.push.Notification;
import com.beeswork.balanceaccountservice.vm.chat.ChatMessageVM;
import org.springframework.messaging.MessageHeaders;

public interface StompService {

    void sendChatMessage(ChatMessageVM chatMessageVM, MessageHeaders messageHeaders);
    void sendPushNotification(Notification notification);
}
