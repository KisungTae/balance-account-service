package com.beeswork.balanceaccountservice.dao.pushtoken;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.pushtoken.PushToken;
import com.beeswork.balanceaccountservice.entity.pushtoken.PushTokenId;

import java.util.UUID;

public interface PushTokenDAO extends BaseDAO<PushToken> {
    PushToken findById(PushTokenId pushTokenId);
    PushToken findRecent(UUID accountId);
}
