package com.beeswork.balanceaccountservice.service.setting;

import com.beeswork.balanceaccountservice.dto.setting.PushSettingDTO;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.UUID;

public interface SettingService {

    PushSettingDTO getPushSetting(UUID accountId, UUID identityToken);

    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1))
    void savePushSettings(UUID accountId, UUID identityToken, Boolean matchPush, Boolean clickedPush, Boolean chatMessagePush);
}