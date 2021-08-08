package com.beeswork.balanceaccountservice.vm.login;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateLoginVM {
    private String accessToken;
    private String refreshToken;
}
