package com.beeswork.balanceaccountservice.service.chat;

import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface ChatService {

    void validateChat(String accountId, String identityToken, String recipientId, String chatId);
    Long validateAndSaveMessage(UUID accountId, UUID identityToken, UUID recipientId, Long chatId, Long messageId, String body, Date createdAt);
    List<ChatMessageDTO> listChatMessages(UUID accountId, UUID identityToken, UUID recipientId, Long chatId, Date lastChatMessageCreatedAt);
    void receivedChatMessages(UUID accountId, UUID identityToken, List<Long> chatMessageIds);
}
