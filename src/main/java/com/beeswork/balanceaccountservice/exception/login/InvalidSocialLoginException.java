package com.beeswork.balanceaccountservice.exception.login;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class InvalidSocialLoginException extends BaseException {

    public InvalidSocialLoginException() {
        super("invalid.social.login.exception");
    }
}
