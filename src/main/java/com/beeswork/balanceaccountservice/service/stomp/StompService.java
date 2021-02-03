package com.beeswork.balanceaccountservice.service.stomp;

import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.vm.chat.ChatMessageVM;
import org.springframework.messaging.MessageHeaders;

import java.util.Locale;

public interface StompService {

    void send(ChatMessageVM chatMessageVM, MessageHeaders messageHeaders);
}
