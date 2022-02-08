package com.beeswork.balanceaccountservice.service.pushtoken;

import com.beeswork.balanceaccountservice.constant.PushTokenType;

import java.util.UUID;

public interface PushTokenService {
    void savePushToken(UUID accountId, String token, PushTokenType type);
}
