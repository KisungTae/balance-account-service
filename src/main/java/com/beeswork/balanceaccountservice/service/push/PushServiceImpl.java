package com.beeswork.balanceaccountservice.service.push;

import com.beeswork.balanceaccountservice.constant.PushTokenType;
import com.beeswork.balanceaccountservice.dao.pushtoken.PushTokenDAO;
import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.entity.pushtoken.PushToken;
import com.beeswork.balanceaccountservice.service.apns.APNSService;
import com.beeswork.balanceaccountservice.service.fcm.FCMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PushServiceImpl implements PushService {

    private final FCMService fcmService;
    private final APNSService apnsService;
    private final PushTokenDAO pushTokenDAO;

    @Autowired
    public PushServiceImpl(FCMService fcmService, APNSService apnsService, PushTokenDAO pushTokenDAO) {
        this.fcmService = fcmService;
        this.apnsService = apnsService;
        this.pushTokenDAO = pushTokenDAO;
    }


    @Override
    public void pushChatMessage(ChatMessageDTO chatMessageDTO) {
        PushToken pushToken = pushTokenDAO.findRecent(chatMessageDTO.getRecipientId());
        if (pushToken == null) return;

    }

    @Override
    public void pushMatch(MatchDTO matchDTO) {

    }
}
