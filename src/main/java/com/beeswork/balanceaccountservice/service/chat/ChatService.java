package com.beeswork.balanceaccountservice.service.chat;

import java.util.Date;

public interface ChatService {

    void validateChat(String accountId, String identityToken, String recipientId, String chatId);
    Long validateAndSaveMessage(String accountId, String identityToken, String recipientId, String chatId, String message, Date createdAt);


}
