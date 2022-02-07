package com.beeswork.balanceaccountservice.service.fcm;

import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.dto.common.Pushable;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import java.util.Locale;

public interface FCMService {

    void sendChatMessage(ChatMessageDTO chatMessageDTO, String token, String senderName, Locale locale);
    void sendMatch(MatchDTO matchDTO, String token, Locale locale);
    void push(Pushable pushable);
}