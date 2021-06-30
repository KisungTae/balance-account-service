package com.beeswork.balanceaccountservice.dao.setting;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.dao.base.BaseDAOImpl;
import com.beeswork.balanceaccountservice.entity.setting.Setting;

import java.util.UUID;

public interface SettingDAO extends BaseDAO<Setting> {
    Setting findByAccountId(UUID accountId);
}
