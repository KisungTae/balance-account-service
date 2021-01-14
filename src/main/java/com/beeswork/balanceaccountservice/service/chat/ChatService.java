package com.beeswork.balanceaccountservice.service.chat;

import java.util.Date;

public interface ChatService {

    void test();
    void validateChat(String accountId, String identityToken, String recipientId, String chatId);
    void validateAndSaveMessage(String accountId, String identityToken, String recipientId, String chatId, String message, Date createdAt);
}
