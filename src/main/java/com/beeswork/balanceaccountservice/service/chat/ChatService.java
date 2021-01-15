package com.beeswork.balanceaccountservice.service.chat;

import com.beeswork.balanceaccountservice.dto.account.MessageReceivedNotificationDTO;
import com.beeswork.balanceaccountservice.vm.chat.ChatMessageVM;

import java.util.Date;

public interface ChatService {


    MessageReceivedNotificationDTO getMessageReceivedNotification(String accountId);
    void validateChat(String accountId, String identityToken, String recipientId, String chatId);
    void validateAndSaveMessage(String accountId, String identityToken, String recipientId, String chatId, String message, Date createdAt);
}
