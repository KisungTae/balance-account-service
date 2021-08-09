package com.beeswork.balanceaccountservice.exception.login;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class RefreshTokenExpiredException extends BaseException {

    private static final String REFRESH_TOKEN_EXPIRED_EXCEPTION = "refresh.token.expired.exception";

    public RefreshTokenExpiredException() {
        super(REFRESH_TOKEN_EXPIRED_EXCEPTION);
    }
}
