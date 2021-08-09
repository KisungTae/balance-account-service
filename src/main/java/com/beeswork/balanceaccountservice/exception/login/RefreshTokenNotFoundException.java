package com.beeswork.balanceaccountservice.exception.login;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class RefreshTokenNotFoundException extends BaseException {

    private static final String REFRESH_TOKEN_NOT_FOUND_EXCEPTION = "refresh.token.not.found.exception";

    public RefreshTokenNotFoundException() {
        super(REFRESH_TOKEN_NOT_FOUND_EXCEPTION);
    }
}
