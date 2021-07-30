package com.beeswork.balanceaccountservice.service.chat;

import com.beeswork.balanceaccountservice.dto.chat.ListChatMessagesDTO;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface ChatService {
    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1))
    Long saveChatMessage(UUID accountId, UUID identityToken, long chatId, UUID recipientId, long key, String body, Date createdAt);
    void syncChatMessages(UUID accountId, UUID identityToken, List<Long> sentChatMessageIds, List<Long> receivedChatMessageIds);
    ListChatMessagesDTO listChatMessages(UUID accountId, UUID identityToken);
}
