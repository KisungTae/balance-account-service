package com.beeswork.balanceaccountservice.dao.account;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.account.PushToken;
import com.beeswork.balanceaccountservice.entity.account.PushTokenId;

public interface PushTokenDAO extends BaseDAO<PushToken> {
    PushToken findById(PushTokenId pushTokenId);
}
