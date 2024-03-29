package com.beeswork.balanceaccountservice.dto.setting;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PushSettingDTO {
    private boolean matchPush;
    private boolean swipePush;
    private boolean chatMessagePush;
    private boolean emailPush;
}
