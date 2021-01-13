package com.beeswork.balanceaccountservice.service.chat;

import java.util.Date;

public interface ChatService {

    void validateChat(String accountId, String identityToken, String matchedId, String chatId);
    void validateAndSaveMessage(String accountId, String identityToken, String matchedId, String chatId, String message, Date createdAt);
}
