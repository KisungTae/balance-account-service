package com.beeswork.balanceaccountservice.service.fcm;

import com.beeswork.balanceaccountservice.constant.PushType;
import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.dto.common.Pushable;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
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
public class FCMServiceImpl implements FCMService {

    private final FirebaseMessaging firebaseMessaging;
    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;

    private static final String PUSH_TYPE = "pushType";

    @Autowired
    public FCMServiceImpl(FirebaseMessaging firebaseMessaging,
                          MessageSource messageSource,
                          ObjectMapper objectMapper) {
        this.firebaseMessaging = firebaseMessaging;
        this.objectMapper = objectMapper;
        this.messageSource = messageSource;
    }

    @Override
    public void push(Pushable pushable, String token, Locale locale) {
        Message.Builder messageBuilder = Message.builder();
        messageBuilder.setToken(token);

        String title = messageSource.getMessage(pushable.getPushTitleId(), pushable.getPushTitleArguments(), locale);
        String body = messageSource.getMessage(pushable.getPushBodyId(), pushable.getPushBodyArguments(), locale);
        messageBuilder.setNotification(Notification.builder().setTitle(title).setBody(body).build());

        Map<String, Object> data = objectMapper.convertValue(pushable, new TypeReference<>() {});
        for (Map.Entry<String, Object> entry: data.entrySet()) {
            messageBuilder.putData(entry.getKey(), entry.getValue().toString());
        }
        messageBuilder.putData(PUSH_TYPE, pushable.getPushType().toString());

        try {
            firebaseMessaging.send(messageBuilder.build());
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }
}
