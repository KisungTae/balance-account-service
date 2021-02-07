package com.beeswork.balanceaccountservice.dao.chatmessage;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.chat.ChatMessage;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface ChatMessageDAO extends BaseDAO<ChatMessage> {

    List<ChatMessage> findAllByChatIdAfter(Long chatId, Date lastChatMessageCreatedAt);
    List<ChatMessage> findAllReceivedAfter(UUID accountId, Date lastChatMessageCreatedAt);
}
