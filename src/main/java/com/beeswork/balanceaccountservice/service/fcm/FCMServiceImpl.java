package com.beeswork.balanceaccountservice.service.fcm;

import com.beeswork.balanceaccountservice.constant.PushTokenType;
import com.beeswork.balanceaccountservice.dao.pushtoken.PushTokenDAO;
import com.beeswork.balanceaccountservice.dto.push.Push;
import com.beeswork.balanceaccountservice.entity.pushtoken.PushToken;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;


@Service
public class FCMServiceImpl extends BaseServiceImpl implements FCMService {

    private final FirebaseMessaging firebaseMessaging;
    private final MessageSource     messageSource;
    private final Message.Builder   messageBuilder;
    private final PushTokenDAO      pushTokenDAO;

    private static final String CLICKED_NOTIFICATION_TITLE = "clicked.notification.title";
    private static final String MATCHED_NOTIFICATION_TITLE = "matched.notification.title";

    @Autowired
    public FCMServiceImpl(FirebaseMessaging firebaseMessaging,
                          MessageSource messageSource,
                          Message.Builder messageBuilder,
                          PushTokenDAO pushTokenDAO) {
        super(null);
        this.firebaseMessaging = firebaseMessaging;
        this.messageSource = messageSource;
        this.messageBuilder = messageBuilder;
        this.pushTokenDAO = pushTokenDAO;
    }


    @Override
    public void sendPush(Push push, Locale locale) {
        try {
            PushToken pushToken = pushTokenDAO.findRecent(push.getAccountId());
            validateAccount(pushToken.getAccount());
            PushTokenType pushTokenType = pushToken.getPushTokenId().getType();

            if (pushTokenType == PushTokenType.APS)
                System.out.println("implement aps notification service");
            else if (pushTokenType == PushTokenType.FCM)
                firebaseMessaging.send(push.buildFCMMessage(messageBuilder,
                                                            messageSource,
                                                            pushToken.getKey(),
                                                            locale));

        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }

}
