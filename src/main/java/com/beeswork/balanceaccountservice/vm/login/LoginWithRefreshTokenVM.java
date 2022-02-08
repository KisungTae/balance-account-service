package com.beeswork.balanceaccountservice.vm.login;

import com.beeswork.balanceaccountservice.constant.PushTokenType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class LoginWithRefreshTokenVM {

    // no validator to throw InvalidJWTTokenException when empty or null
    private String accessToken;
    private String refreshToken;
    private String pushToken;

    @NotNull
    private PushTokenType pushTokenType;


}
