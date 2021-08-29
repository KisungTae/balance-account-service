package com.beeswork.balanceaccountservice.exception.login;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class AccessTokenExpiredException extends BaseException {

    private static final String ACCESS_TOKEN_EXPIRED_EXCEPTION = "expired.jwt.exception";

    public AccessTokenExpiredException() {
        super(ACCESS_TOKEN_EXPIRED_EXCEPTION);
    }
}
