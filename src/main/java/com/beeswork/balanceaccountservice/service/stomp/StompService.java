package com.beeswork.balanceaccountservice.service.stomp;

import com.beeswork.balanceaccountservice.dto.push.Push;
import com.beeswork.balanceaccountservice.vm.chat.ChatMessageVM;
import org.springframework.messaging.MessageHeaders;

import java.util.Locale;

public interface StompService {

    void sendChatMessage(ChatMessageVM chatMessageVM, MessageHeaders messageHeaders);
    void sendPush(Push push, Locale locale);
}
