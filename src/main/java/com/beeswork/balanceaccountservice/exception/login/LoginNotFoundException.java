package com.beeswork.balanceaccountservice.exception.login;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class LoginNotFoundException extends BaseException {

    private static final String CODE = "login.not.found.exception";

    public LoginNotFoundException() {
        super(CODE);
    }
}
