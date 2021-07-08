package com.beeswork.balanceaccountservice.service.push;

import com.beeswork.balanceaccountservice.constant.PushTokenType;
import com.beeswork.balanceaccountservice.constant.PushType;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.pushtoken.PushTokenDAO;
import com.beeswork.balanceaccountservice.dao.setting.SettingDAO;
import com.beeswork.balanceaccountservice.dto.chat.ChatMessageDTO;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.pushtoken.PushToken;
import com.beeswork.balanceaccountservice.entity.setting.Setting;
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
    private final AccountDAO accountDAO;
    private final SettingDAO settingDAO;

    @Autowired
    public PushServiceImpl(FCMService fcmService,
                           APNSService apnsService,
                           PushTokenDAO pushTokenDAO,
                           AccountDAO accountDAO, SettingDAO settingDAO) {
        this.fcmService = fcmService;
        this.apnsService = apnsService;
        this.pushTokenDAO = pushTokenDAO;
        this.accountDAO = accountDAO;
        this.settingDAO = settingDAO;
    }

    @Override
    public void pushChatMessage(ChatMessageDTO chatMessageDTO, Locale locale) {
        PushToken pushToken = pushTokenDAO.findRecent(chatMessageDTO.getRecipientId());
        if (pushToken == null) return;

        Setting setting = settingDAO.findByAccountId(chatMessageDTO.getRecipientId());
        if (setting != null && !setting.isChatMessagePush()) return;

        Account sender = accountDAO.findById(chatMessageDTO.getAccountId());
        if (sender == null) return;
        if (pushToken.getType() == PushTokenType.APS) apnsService.sendChatMessage(chatMessageDTO, locale);
        else fcmService.sendChatMessage(chatMessageDTO, pushToken.getToken(), sender.getName(), locale);
    }

    @Override
    public void pushMatch(MatchDTO matchDTO, Locale locale) {
        if (matchDTO == null || matchDTO.getSwipedId() == null) return;
        PushToken pushToken = pushTokenDAO.findRecent(matchDTO.getSwipedId());
        if (pushToken == null) return;

        Setting setting = settingDAO.findByAccountId(matchDTO.getSwipedId());
        if (setting != null) {
            if (matchDTO.getPushType() == PushType.CLICKED && !setting.isClickedPush()) return;
            if (matchDTO.getPushType() == PushType.MATCHED && !setting.isMatchPush()) return;
        }
        if (matchDTO.getPushType() == PushType.MATCHED) matchDTO.swapIds();

        if (pushToken.getType() == PushTokenType.APS) apnsService.sendMatch(matchDTO, locale);
        else if (pushToken.getType() == PushTokenType.FCM) fcmService.sendMatch(matchDTO, pushToken.getToken(), locale);
    }
}
