package com.beeswork.balanceaccountservice.exception.login;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class RefreshTokenKeyNotFoundException extends BaseException {

    public static final String REFRESH_TOKEN_KEY_NOT_FOUND_EXCEPTION = "refresh.token.key.not.found.exception";

    public RefreshTokenKeyNotFoundException() {
        super(REFRESH_TOKEN_KEY_NOT_FOUND_EXCEPTION);
    }
}
