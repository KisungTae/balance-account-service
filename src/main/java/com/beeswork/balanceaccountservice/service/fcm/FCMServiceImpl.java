package com.beeswork.balanceaccountservice.service.fcm;

import com.beeswork.balanceaccountservice.constant.PushType;
import com.beeswork.balanceaccountservice.dao.pushtoken.PushTokenDAO;
import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.dto.common.Pushable;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.service.base.BasePushServiceImpl;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;


@Service
public class FCMServiceImpl extends BasePushServiceImpl implements FCMService {

    private final FirebaseMessaging firebaseMessaging;
    private final ObjectMapper objectMapper;

    @Autowired
    public FCMServiceImpl(FirebaseMessaging firebaseMessaging,
                          MessageSource messageSource,
                          ObjectMapper objectMapper) {
        super(messageSource);
        this.firebaseMessaging = firebaseMessaging;
        this.objectMapper = objectMapper;
    }

    @Override
    public void sendChatMessage(ChatMessageDTO chatMessageDTO, String token, String senderName, Locale locale) {
        Message.Builder messageBuilder = Message.builder();
        setNotification(PushType.CHAT_MESSAGE, token, new String[] {senderName}, locale, messageBuilder);
        setData(objectMapper.convertValue(chatMessageDTO, new TypeReference<>() {}), messageBuilder);
        sendMessage(messageBuilder.build());
    }

    private void setNotification(PushType pushType, String token, String[] arguments, Locale locale, Message.Builder messageBuilder) {
        String title = getTitle(pushType, locale);
        String body = getBody(pushType, arguments, locale);
        messageBuilder.setNotification(Notification.builder().setTitle(title).setBody(body).build());
        messageBuilder.setToken(token);
    }

    private void setData(Map<String, Object> data, Message.Builder messageBuilder) {
        for (Map.Entry<String, Object> entry: data.entrySet()) {
            messageBuilder.putData(entry.getKey(), entry.getValue().toString());
        }
    }

    private void sendMessage(Message message) {
        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMatch(MatchDTO matchDTO, String token, Locale locale) {
        Message.Builder messageBuilder = Message.builder();
        setNotification(matchDTO.getPushType(), token, new String[] {matchDTO.getName()}, locale, messageBuilder);
        setData(objectMapper.convertValue(matchDTO, new TypeReference<>() {}), messageBuilder);
        sendMessage(messageBuilder.build());
    }

    @Override
    public void push(Pushable pushable) {

    }
}
