package com.beeswork.balanceaccountservice.service.chat;

import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface ChatService {

    long UNMATCHED = -1;

    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1))
    Long saveChatMessage(UUID accountId, UUID identityToken, long chatId, UUID recipientId, long key, String body, Date createdAt);
    void receivedChatMessage(UUID accountId, UUID identityToken, Long chatMessageId);
    void fetchedChatMessage(UUID accountId, UUID identityToken, Long chatMessageId);
    void syncChatMessages(UUID accountId, UUID identityToken, List<Long> sentChatMessageIds, List<Long> receivedChatMessageIds);
}
