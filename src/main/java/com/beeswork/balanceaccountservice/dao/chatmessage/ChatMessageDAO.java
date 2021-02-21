package com.beeswork.balanceaccountservice.dao.chatmessage;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.entity.chat.ChatMessage;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface ChatMessageDAO extends BaseDAO<ChatMessage> {
    List<ChatMessageDTO> findAllUnreceived(UUID accountId);
    List<ChatMessageDTO> findAllUnfetched(UUID accountId);
    List<ChatMessage> findAllIn(List<Long> chatMessageIds);
    ChatMessage findById(Long chatMessageId);
    ChatMessage findByMessageId(Long messageId);
}
