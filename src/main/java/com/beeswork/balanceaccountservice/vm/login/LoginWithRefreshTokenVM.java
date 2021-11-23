package com.beeswork.balanceaccountservice.vm.login;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class LoginWithRefreshTokenVM {

    // no validator to throw InvalidJWTTokenException when empty or null
    private String accessToken;
    private String refreshToken;


}
