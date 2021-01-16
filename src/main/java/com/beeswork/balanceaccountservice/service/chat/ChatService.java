package com.beeswork.balanceaccountservice.service.chat;

import com.beeswork.balanceaccountservice.dto.firebase.MessageReceivedNotificationDTO;
import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import org.springframework.messaging.MessageHeaders;

import java.util.Date;
import java.util.Locale;

public interface ChatService {

    void validateChat(String accountId, String identityToken, String recipientId, String chatId);
    void validateAndSaveMessage(String accountId, String identityToken, String recipientId, String chatId, String message, Date createdAt);


}
