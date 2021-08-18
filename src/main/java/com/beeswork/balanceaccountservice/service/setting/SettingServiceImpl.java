package com.beeswork.balanceaccountservice.service.setting;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.setting.PushSettingDAO;
import com.beeswork.balanceaccountservice.dto.setting.PushSettingDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.setting.PushSetting;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SettingServiceImpl extends BaseServiceImpl implements SettingService {

    private final PushSettingDAO pushSettingDAO;
    private final AccountDAO     accountDAO;
    private final ModelMapper modelMapper;

    @Autowired
    public SettingServiceImpl(PushSettingDAO pushSettingDAO, AccountDAO accountDAO, ModelMapper modelMapper) {
        this.pushSettingDAO = pushSettingDAO;
        this.accountDAO = accountDAO;
        this.modelMapper = modelMapper;
    }

    @Override
    public PushSettingDTO getPushSetting(UUID accountId) {
        PushSetting pushSetting = pushSettingDAO.findByAccountId(accountId);
        return modelMapper.map(pushSetting, PushSettingDTO.class);
    }

    @Override
    @Transactional
    public void savePushSettings(UUID accountId, Boolean matchPush, Boolean clickedPush, Boolean chatMessagePush) {
        Account account = accountDAO.findById(accountId);
        PushSetting pushSetting = pushSettingDAO.findByAccountId(account.getId());
        if (pushSetting == null) pushSetting = new PushSetting(account);

        if (matchPush != null) pushSetting.setMatchPush(matchPush);
        if (clickedPush != null) pushSetting.setClickedPush(clickedPush);
        if (chatMessagePush != null) pushSetting.setChatMessagePush(chatMessagePush);

        pushSettingDAO.persist(pushSetting);
    }
}
