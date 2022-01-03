package com.beeswork.balanceaccountservice.dao.chat;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.entity.chat.SentChatMessage;

import java.util.List;
import java.util.UUID;

public interface SentChatMessageDAO extends BaseDAO<SentChatMessage> {

    List<ChatMessageDTO> findAllUnfetched(UUID accountId);
    List<SentChatMessage> findAllIn(List<UUID> chatMessageIds);
    SentChatMessage findById(UUID chatMessageId);
}
