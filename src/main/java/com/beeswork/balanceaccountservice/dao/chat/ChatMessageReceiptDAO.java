package com.beeswork.balanceaccountservice.dao.chat;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.chat.ChatMessageReceipt;

import java.util.List;
import java.util.UUID;

public interface ChatMessageReceiptDAO extends BaseDAO<ChatMessageReceipt> {
    ChatMessageReceipt findBy(UUID accountId, UUID chatId, long chatMessageId);
    List<ChatMessageReceipt> findAllBy(UUID accountId, UUID chatId, List<Long> chatMessageIds);
}
