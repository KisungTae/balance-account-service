package com.beeswork.balanceaccountservice.exception.login;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class InvalidRefreshTokenException extends BaseException {

    private static final String INVALID_REFRESH_TOKEN_EXCEPTION = "invalid.refresh.token.exception";

    public InvalidRefreshTokenException() {
        super(INVALID_REFRESH_TOKEN_EXCEPTION);
    }
}
