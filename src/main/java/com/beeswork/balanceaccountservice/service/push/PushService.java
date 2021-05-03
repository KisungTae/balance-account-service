package com.beeswork.balanceaccountservice.service.push;

import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import org.apache.tomcat.jni.Local;

import java.util.Locale;

public interface PushService {

    void pushChatMessage(ChatMessageDTO chatMessageDTO, Locale locale);
    void pushMatch(MatchDTO matchDTO, Locale locale);
}
