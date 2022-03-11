package com.beeswork.balanceaccountservice.service.chat;

import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.dto.chat.ListChatMessagesDTO;
import com.beeswork.balanceaccountservice.dto.chat.SaveChatMessageDTO;

import java.util.List;
import java.util.UUID;

public interface ChatService {

    List<ChatMessageDTO> fetchChatMessages(UUID senderId, UUID chatId, Long lastChatMessageId, int loadSize);

    SaveChatMessageDTO saveChatMessage(ChatMessageDTO chatMessageDTO);
    void syncChatMessages(List<UUID> sentChatMessageIds, List<UUID> receivedChatMessageIds);
    ListChatMessagesDTO listChatMessages(UUID accountId);
    void fetchedChatMessage(UUID accountId, UUID chatMessageId);
    void receivedChatMessage(UUID accountId, UUID chatMessageId);

}
