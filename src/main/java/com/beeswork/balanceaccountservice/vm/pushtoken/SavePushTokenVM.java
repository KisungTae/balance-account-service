package com.beeswork.balanceaccountservice.vm.pushtoken;

import com.beeswork.balanceaccountservice.vm.account.AccountIdentityVM;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class SavePushTokenVM extends AccountIdentityVM {

    @NotEmpty
    private String key;
}
