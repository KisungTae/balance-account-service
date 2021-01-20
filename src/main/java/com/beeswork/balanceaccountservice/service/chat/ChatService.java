package com.beeswork.balanceaccountservice.service.chat;

import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface ChatService {

    void validateChat(String accountId, String identityToken, String recipientId, String chatId);
    Long validateAndSaveMessage(ChatMessageDTO chatMessageDTO, String identityToken, String messageId);
    List<ChatMessageDTO> listChatMessages(UUID accountId, UUID identityToken, UUID recipientId, Long chatId, Long lastChatMessageId);
}
