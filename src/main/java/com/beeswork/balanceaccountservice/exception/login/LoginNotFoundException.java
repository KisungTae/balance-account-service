package com.beeswork.balanceaccountservice.exception.login;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class LoginNotFoundException extends BaseException {

    private static final String LOGIN_NOT_FOUND_EXCEPTION = "login.not.found.exception";

    public LoginNotFoundException() {
        super(LOGIN_NOT_FOUND_EXCEPTION);
    }
}
