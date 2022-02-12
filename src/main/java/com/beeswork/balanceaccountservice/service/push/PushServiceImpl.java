package com.beeswork.balanceaccountservice.service.push;

import com.beeswork.balanceaccountservice.constant.PushTokenType;
import com.beeswork.balanceaccountservice.constant.PushType;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.pushtoken.PushTokenDAO;
import com.beeswork.balanceaccountservice.dao.setting.PushSettingDAO;
import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.dto.common.Pushable;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.pushtoken.PushToken;
import com.beeswork.balanceaccountservice.entity.setting.PushSetting;
import com.beeswork.balanceaccountservice.service.apns.APNSService;
import com.beeswork.balanceaccountservice.service.fcm.FCMService;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;

@Service
public class PushServiceImpl implements PushService {

    private final FCMService     fcmService;
    private final APNSService    apnsService;
    private final PushTokenDAO   pushTokenDAO;
    private final AccountDAO     accountDAO;
    private final PushSettingDAO pushSettingDAO;

    @Autowired
    public PushServiceImpl(FCMService fcmService,
                           APNSService apnsService,
                           PushTokenDAO pushTokenDAO,
                           AccountDAO accountDAO, PushSettingDAO pushSettingDAO) {
        this.fcmService = fcmService;
        this.apnsService = apnsService;
        this.pushTokenDAO = pushTokenDAO;
        this.accountDAO = accountDAO;
        this.pushSettingDAO = pushSettingDAO;
    }

    @Override
    public void pushChatMessage(ChatMessageDTO chatMessageDTO, Locale locale) {
        if (chatMessageDTO == null) {
            return;
        }
        PushToken pushToken = getPushToken(chatMessageDTO);
        if (pushToken == null || StringUtils.isBlank(pushToken.getToken())) {
            return;
        }
        Account sender = accountDAO.findById(chatMessageDTO.getAccountId());
        chatMessageDTO.setSenderName(sender.getName());
        push(chatMessageDTO, pushToken, locale);
    }

    @Override
    public void push(Pushable pushable, Locale locale) {
        if (pushable == null) {
            return;
        }
        PushToken pushToken = getPushToken(pushable);
        if (pushToken == null || StringUtils.isBlank(pushToken.getToken())) {
            return;
        }
        push(pushable, pushToken, locale);
    }

    private void push(Pushable pushable, PushToken pushToken, Locale locale) {
        if (pushToken.getType() == PushTokenType.APS) {
            apnsService.push(pushable, locale);
        } else if (pushToken.getType() == PushTokenType.FCM) {
            fcmService.push(pushable, pushToken.getToken(), locale);
        }
    }

    private PushToken getPushToken(Pushable pushable) {
        PushToken pushToken = pushTokenDAO.findRecentByAccountId(pushable.getRecipientId());
        if (pushToken == null || !pushToken.isActive()) {
            return null;
        }

        PushSetting pushSetting = pushSettingDAO.findByAccountId(pushable.getRecipientId());
        if (pushSetting != null) {
            if (pushable.getPushType() == PushType.SWIPE && !pushSetting.isSwipePush()) {
                return null;
            }
            if (pushable.getPushType() == PushType.MATCH && !pushSetting.isMatchPush()) {
                return null;
            }
            if (pushable.getPushType() == PushType.CHAT_MESSAGE && !pushSetting.isChatMessagePush()) {
                return null;
            }
        }
        return pushToken;
    }
}
