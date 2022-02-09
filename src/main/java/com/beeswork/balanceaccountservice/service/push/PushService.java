package com.beeswork.balanceaccountservice.service.push;

import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.dto.common.Pushable;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;

import java.util.Locale;

public interface PushService {
    void pushChatMessage(ChatMessageDTO chatMessageDTO, Locale locale);
    void push(Pushable pushable, Locale locale);
}
