package com.beeswork.balanceaccountservice.service.fcm;

import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.dto.common.Pushable;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import java.util.Locale;

public interface FCMService {
    void push(Pushable pushable, String token, Locale locale);
}