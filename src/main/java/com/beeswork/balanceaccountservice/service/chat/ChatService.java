package com.beeswork.balanceaccountservice.service.chat;

public interface ChatService {

    void checkIfValidMatch(String accountId, String identityToken, String matchedId, Long chatId);
}
