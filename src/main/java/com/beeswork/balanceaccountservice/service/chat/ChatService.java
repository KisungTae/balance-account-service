package com.beeswork.balanceaccountservice.service.chat;

import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.dto.chat.SaveChatMessageDTO;

import java.util.List;
import java.util.UUID;

public interface ChatService {

    List<ChatMessageDTO> fetchChatMessages(UUID senderId, UUID chatId, Long lastChatMessageId, int loadSize);
    List<ChatMessageDTO> listChatMessages(UUID senderId, UUID chatId, UUID appToken, int startPosition, int loadSize);
    void syncChatMessages(UUID accountId, UUID chatId, UUID appToken, List<Long> chatMessageIds);

    SaveChatMessageDTO saveChatMessage(ChatMessageDTO chatMessageDTO);

}
