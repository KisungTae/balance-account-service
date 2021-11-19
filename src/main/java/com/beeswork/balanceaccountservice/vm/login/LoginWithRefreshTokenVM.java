package com.beeswork.balanceaccountservice.vm.login;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class LoginWithRefreshTokenVM {

    @NotEmpty
    private String refreshToken;
}
