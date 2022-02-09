package com.beeswork.balanceaccountservice.dto.common;

import com.beeswork.balanceaccountservice.constant.PushType;

import java.util.UUID;

public interface Pushable {

    UUID getRecipientId();
    PushType getPushType();
    String[] getPushTitleArguments();
    String[] getPushBodyArguments();
    String getPushTitleId();
    String getPushBodyId();
}
