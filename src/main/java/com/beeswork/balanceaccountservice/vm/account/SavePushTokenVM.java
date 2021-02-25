package com.beeswork.balanceaccountservice.vm.account;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class SavePushTokenVM extends AccountIdentityVM {

    @NotEmpty
    private String key;
}
