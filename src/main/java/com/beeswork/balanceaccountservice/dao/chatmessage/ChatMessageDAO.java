package com.beeswork.balanceaccountservice.dao.chatmessage;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.chat.ChatMessage;

import java.util.List;

public interface ChatMessageDAO extends BaseDAO<ChatMessage> {

    List<ChatMessage> findAllByChatIdAfter(Long chatId, Long lastChatMessageId);
}
