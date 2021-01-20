package com.beeswork.balanceaccountservice.service.chat;

import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;

import java.util.Date;

public interface ChatService {

    void validateChat(String accountId, String identityToken, String recipientId, String chatId);
    Long validateAndSaveMessage(ChatMessageDTO chatMessageDTO, String identityToken, String messageId);

}
