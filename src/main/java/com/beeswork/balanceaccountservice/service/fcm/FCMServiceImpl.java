package com.beeswork.balanceaccountservice.service.fcm;

import com.beeswork.balanceaccountservice.dao.pushtoken.PushTokenDAO;
import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;


@Service
public class FCMServiceImpl extends BaseServiceImpl implements FCMService {

    private final FirebaseMessaging firebaseMessaging;
    private final MessageSource messageSource;
    private final Message.Builder messageBuilder;
    private final PushTokenDAO pushTokenDAO;
    private final ObjectMapper objectMapper;

    @Autowired
    public FCMServiceImpl(FirebaseMessaging firebaseMessaging,
                          MessageSource messageSource,
                          Message.Builder messageBuilder,
                          PushTokenDAO pushTokenDAO, ObjectMapper objectMapper) {
        super(null);
        this.firebaseMessaging = firebaseMessaging;
        this.messageSource = messageSource;
        this.messageBuilder = messageBuilder;
        this.pushTokenDAO = pushTokenDAO;
        this.objectMapper = objectMapper;
    }


    @Override
    public void sendChatMessage(ChatMessageDTO chatMessageDTO, String token, Locale locale) {

//        if (push == null) return;
//        try {
//            PushToken pushToken = pushTokenDAO.findRecent(push.getAccountId());
//            validateAccount(pushToken.getAccount());
//            String key = pushToken.getKey();
//            if (key == null || key.isEmpty()) return;
//
//            PushTokenType pushTokenType = pushToken.getPushTokenId().getType();
//            if (pushTokenType == PushTokenType.APS)
//                System.out.println("implement aps notification service");
//            else if (pushTokenType == PushTokenType.FCM)
//                firebaseMessaging.send(push.buildFCMMessage(messageBuilder, messageSource, key, locale));
//        } catch (FirebaseMessagingException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void sendMatch(MatchDTO matchDTO, String token, Locale locale) {
        Map map = objectMapper.convertValue(matchDTO, Map.class);
        System.out.println(map);
    }
}
