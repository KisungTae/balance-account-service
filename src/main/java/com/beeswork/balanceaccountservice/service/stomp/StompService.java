package com.beeswork.balanceaccountservice.service.stomp;

import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.dto.push.Push;
import com.beeswork.balanceaccountservice.vm.chat.ChatMessageVM;
import org.springframework.messaging.MessageHeaders;

import java.util.Locale;

public interface StompService {

    void sendChatMessage(ChatMessageDTO chatMessageDTO, MessageHeaders messageHeaders);
    void sendMatch(MatchDTO matchDTO);
    void sendPush(Push push, Locale locale);
}
