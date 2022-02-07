package com.beeswork.balanceaccountservice.service.stomp;

import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.dto.common.Pushable;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import org.springframework.messaging.MessageHeaders;

import java.util.Locale;

public interface StompService {

    void sendChatMessage(ChatMessageDTO chatMessageDTO, Locale locale);
    void sendMatch(MatchDTO matchDTO, Locale locale);

    void push(Pushable pushable);
}
