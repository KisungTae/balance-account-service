package com.beeswork.balanceaccountservice.service.chat;

public interface ChatService {

    String checkIfValidChat(String accountId, String identityToken, String chatId);
}
