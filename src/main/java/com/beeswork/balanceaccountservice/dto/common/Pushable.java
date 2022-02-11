package com.beeswork.balanceaccountservice.dto.common;

import com.beeswork.balanceaccountservice.constant.PushType;

import java.util.UUID;

public interface Pushable {

    PushType getPushType();
    UUID getRecipientId();
    String[] getPushTitleArguments();
    String[] getPushBodyArguments();
    String getPushTitleId();
    String getPushBodyId();
}
