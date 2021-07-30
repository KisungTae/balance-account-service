package com.beeswork.balanceaccountservice.dao.setting;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.setting.PushSetting;

import java.util.UUID;

public interface PushSettingDAO extends BaseDAO<PushSetting> {
    PushSetting findByAccountId(UUID accountId);
}
