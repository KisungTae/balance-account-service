package com.beeswork.balanceaccountservice.service.setting;

import com.beeswork.balanceaccountservice.dto.setting.PushSettingDTO;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.UUID;

public interface SettingService {

    PushSettingDTO getPushSetting(UUID accountId);
    void savePushSettings(UUID accountId, Boolean matchPush, Boolean swipePush, Boolean chatMessagePush);
}
