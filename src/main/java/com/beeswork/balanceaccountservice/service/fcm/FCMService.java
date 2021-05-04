package com.beeswork.balanceaccountservice.service.fcm;

import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.dto.push.Push;
import com.google.firebase.messaging.FirebaseMessagingException;

import java.util.Locale;

public interface FCMService {

    void sendChatMessage(ChatMessageDTO chatMessageDTO, String token, String senderName, Locale locale);
    void sendMatch(MatchDTO matchDTO, String token, Locale locale);
}