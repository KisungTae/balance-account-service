package com.beeswork.balanceaccountservice.vm.login;

import com.beeswork.balanceaccountservice.constant.LoginType;
import com.beeswork.balanceaccountservice.constant.PushTokenType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class SocialLoginVM {

    @NotEmpty
    private String accessToken;

    @NotEmpty
    private String loginId;

    @NotNull
    private LoginType loginType;

    private String pushToken;

    @NotNull
    private PushTokenType pushTokenType;
}
