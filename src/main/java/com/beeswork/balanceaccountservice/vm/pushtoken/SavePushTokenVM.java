package com.beeswork.balanceaccountservice.vm.pushtoken;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class SavePushTokenVM {

    @NotEmpty
    private String token;
}
