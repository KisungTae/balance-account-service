package com.beeswork.balanceaccountservice.service.apns;

import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class APNSServiceImpl implements APNSService {

    @Override
    public void sendChatMessage(ChatMessageDTO chatMessageDTO, Locale locale) {

    }

    @Override
    public void sendMatch(MatchDTO matchDTO, Locale locale) {

    }
}
