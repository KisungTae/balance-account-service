package com.beeswork.balanceaccountservice.service.chat;

import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface ChatService {

    void validateChat(String accountId, String identityToken, String recipientId, String chatId);

    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1))
    ChatMessageDTO saveChatMessage(UUID accountId, UUID identityToken, UUID recipientId, long chatId, long messageId, String body, Date createdAt);

    void syncChatMessages(UUID accountId, UUID identityToken, List<Long> chatMessageIds) throws InterruptedException;

    void receivedChatMessage(UUID accountId, UUID identityToken, Long chatMessageId);
    void fetchedChatMessage(UUID accountId, UUID identityToken, Long chatMessageId);
}
