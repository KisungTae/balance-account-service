package com.beeswork.balanceaccountservice.exception.login;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class InvalidSocialLoginException extends BaseException {

    private static final String CODE = "invalid.social.login.exception";

    public InvalidSocialLoginException() {
        super(CODE);
    }
}
