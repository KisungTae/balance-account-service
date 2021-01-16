package com.beeswork.balanceaccountservice.service.stomp;

import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;

import java.util.Locale;

public interface StompService {

    void send(ChatMessageDTO chatMessageDTO, Locale locale);
}
