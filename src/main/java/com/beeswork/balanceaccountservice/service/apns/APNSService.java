package com.beeswork.balanceaccountservice.service.apns;

import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;

import java.util.Locale;

public interface APNSService {
    void sendChatMessage(ChatMessageDTO chatMessageDTO, Locale locale);
    void sendMatch(MatchDTO matchDTO, Locale locale);
}
