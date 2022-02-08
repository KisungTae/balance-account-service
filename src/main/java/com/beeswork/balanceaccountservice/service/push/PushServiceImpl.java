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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class PushServiceImpl implements PushService {

    private final FCMService fcmService;
    private final APNSService apnsService;
    private final PushTokenDAO pushTokenDAO;
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
        PushToken pushToken = pushTokenDAO.findRecentByAccountId(chatMessageDTO.getRecipientId());
        if (pushToken == null) return;

        PushSetting pushSetting = pushSettingDAO.findByAccountId(chatMessageDTO.getRecipientId());
        if (pushSetting != null && !pushSetting.isChatMessagePush()) return;

        Account sender = accountDAO.findById(chatMessageDTO.getAccountId());
        if (sender == null) return;
        if (pushToken.getType() == PushTokenType.APS) apnsService.sendChatMessage(chatMessageDTO, locale);
        else fcmService.sendChatMessage(chatMessageDTO, pushToken.getToken(), sender.getName(), locale);
    }

    @Override
    public void pushMatch(MatchDTO matchDTO, Locale locale) {
        if (matchDTO == null || matchDTO.getSwipedId() == null) return;

        PushToken pushToken = pushTokenDAO.findRecentByAccountId(matchDTO.getSwipedId());
        if (pushToken == null || !pushToken.isActive()) return;

        PushSetting pushSetting = pushSettingDAO.findByAccountId(matchDTO.getSwipedId());
        if (pushSetting != null) {
            if (matchDTO.getPushType() == PushType.CLICKED && !pushSetting.isClickedPush()) return;
            if (matchDTO.getPushType() == PushType.MATCHED && !pushSetting.isMatchPush()) return;
        }
        if (matchDTO.getPushType() == PushType.MATCHED) matchDTO.swapIds();

        if (pushToken.getType() == PushTokenType.APS) apnsService.sendMatch(matchDTO, locale);
        else if (pushToken.getType() == PushTokenType.FCM) fcmService.sendMatch(matchDTO, pushToken.getToken(), locale);
    }

    @Override
    public void push(Pushable pushable) {
        if (pushable == null) {
            return;
        }
        PushToken pushToken = pushTokenDAO.findRecentByAccountId(pushable.getRecipientId());
        if (pushToken == null || !pushToken.isActive()) {
            return;
        }

        PushSetting pushSetting = pushSettingDAO.findByAccountId(pushable.getRecipientId());




    }
}
