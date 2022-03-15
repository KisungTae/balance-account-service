package com.beeswork.balanceaccountservice.dao.chat;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.chat.ChatMessageReceipt;

import java.util.UUID;

public interface ChatMessageReceiptDAO extends BaseDAO<ChatMessageReceipt> {
    ChatMessageReceipt findBy(UUID accountId, UUID chatId, long chatMessageId);
}
