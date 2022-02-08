package com.beeswork.balanceaccountservice.vm.pushtoken;

import com.beeswork.balanceaccountservice.constant.PushTokenType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class SavePushTokenVM {

    @NotEmpty
    private String token;

    @NotNull
    private PushTokenType type;
}
