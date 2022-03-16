package com.beeswork.balanceaccountservice.dao.chat;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.chat.ChatMessage;

import java.util.List;
import java.util.UUID;

public interface ChatMessageDAO extends BaseDAO<ChatMessage> {
    List<ChatMessage> findAllBy(UUID chatId, Long lastChatMessageId, int loadSize);
    List<ChatMessage> findAllBy(UUID senderId, UUID chatId, UUID appToken, int startPosition, int loadSize);
    ChatMessage findBy(UUID chatId, Long chatMessageId);
}
