package com.beeswork.balanceaccountservice.service.chat;

import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.dto.chat.ListChatMessagesDTO;
import com.beeswork.balanceaccountservice.dto.chat.SaveChatMessageDTO;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.List;
import java.util.UUID;

public interface ChatService {
    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1))
    SaveChatMessageDTO saveChatMessage(ChatMessageDTO chatMessageDTO);

    void syncChatMessages(List<UUID> sentChatMessageIds, List<UUID> receivedChatMessageIds);
    ListChatMessagesDTO listChatMessages(UUID accountId);
    void fetchedChatMessage(UUID accountId, UUID chatMessageId);
    void receivedChatMessage(UUID accountId, UUID chatMessageId);

}
