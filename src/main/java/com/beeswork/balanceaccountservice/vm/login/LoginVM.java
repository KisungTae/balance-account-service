package com.beeswork.balanceaccountservice.vm.login;

import com.beeswork.balanceaccountservice.constant.LoginType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class LoginVM {

    @NotEmpty
    private String loginId;

    @NotEmpty
    private String password;
}
