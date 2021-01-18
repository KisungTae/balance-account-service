package com.beeswork.balanceaccountservice.service.stomp;

import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import org.springframework.messaging.MessageHeaders;

import java.util.Locale;

public interface StompService {

    void send(ChatMessageDTO chatMessageDTO, MessageHeaders messageHeaders);
}
