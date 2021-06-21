package com.beeswork.balanceaccountservice.vm.setting;

import com.beeswork.balanceaccountservice.vm.account.AccountIdentityVM;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SavePushSettingsVM extends AccountIdentityVM {
    private Boolean matchPush;
    private Boolean clickedPush;
    private Boolean chatMessagePush;
}
