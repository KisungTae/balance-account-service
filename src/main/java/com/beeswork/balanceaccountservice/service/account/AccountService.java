package com.beeswork.balanceaccountservice.service.account;

import com.beeswork.balanceaccountservice.constant.PushTokenType;
import com.beeswork.balanceaccountservice.dto.account.*;
import org.locationtech.jts.geom.Point;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AccountService {
    void savePushToken(UUID accountId, UUID identityToken, String key, PushTokenType type);
    void saveAnswers(UUID accountId, UUID identityToken, Map<Integer, Boolean> answers);
    void saveEmail(UUID accountId, UUID identityToken, String email);
    void deleteAccount(UUID accountId, UUID identityToken);
}