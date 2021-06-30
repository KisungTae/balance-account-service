package com.beeswork.balanceaccountservice.service.setting;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.setting.SettingDAO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.setting.Setting;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SettingServiceImpl extends BaseServiceImpl implements SettingService {

    private final SettingDAO settingDAO;
    private final AccountDAO accountDAO;

    @Autowired
    public SettingServiceImpl(SettingDAO settingDAO, AccountDAO accountDAO) {
        this.settingDAO = settingDAO;
        this.accountDAO = accountDAO;
    }

    @Override
    @Transactional
    public void savePushSettings(UUID accountId, UUID identityToken, Boolean matchPush, Boolean clickedPush, Boolean chatMessagePush) {
        Account account = accountDAO.findById(accountId);
        validateAccount(account, identityToken);
        Setting setting = settingDAO.findByAccountId(account.getId());
        if (setting == null) setting = new Setting(account);

        if (matchPush != null) setting.setMatchPush(matchPush);
        if (clickedPush != null) setting.setClickedPush(clickedPush);
        if (chatMessagePush != null) setting.setChatMessagePush(chatMessagePush);

        settingDAO.persist(setting);
    }
}
