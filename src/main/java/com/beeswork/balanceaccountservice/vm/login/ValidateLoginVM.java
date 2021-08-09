package com.beeswork.balanceaccountservice.vm.login;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class ValidateLoginVM {

    @NotEmpty
    private String accessToken;

    @NotEmpty
    private String refreshToken;
}
