package com.beeswork.balanceaccountservice.vm.setting;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SavePushSettingsVM {
    private Boolean matchPush;
    private Boolean clickedPush;
    private Boolean chatMessagePush;
}
