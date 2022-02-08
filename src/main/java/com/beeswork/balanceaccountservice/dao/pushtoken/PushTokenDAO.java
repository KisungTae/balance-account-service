package com.beeswork.balanceaccountservice.dao.pushtoken;

import com.beeswork.balanceaccountservice.constant.PushTokenType;
import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.pushtoken.PushToken;
import com.beeswork.balanceaccountservice.entity.pushtoken.PushTokenId;

import java.util.List;
import java.util.UUID;

public interface PushTokenDAO extends BaseDAO<PushToken> {
    PushToken findById(PushTokenId pushTokenId);
    PushToken findRecentByAccountId(UUID accountId);
    List<PushToken> findAllBy(String token, PushTokenType pushTokenType);
}
