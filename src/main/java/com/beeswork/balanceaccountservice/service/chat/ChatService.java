package com.beeswork.balanceaccountservice.service.chat;

public interface ChatService {

    void checkIfValidChat(String accountId, String identityToken, String matchedId, String chatId);
}
