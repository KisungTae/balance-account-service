package com.beeswork.balanceaccountservice.vm.login;

import com.beeswork.balanceaccountservice.constant.LoginType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class SocialLoginVM {

    private String    idToken;
    private String    accessToken;

    @NotEmpty
    private String    loginId;

    @NotNull
    private LoginType loginType;
}
