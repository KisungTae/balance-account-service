package com.beeswork.balanceaccountservice.exception.jwt;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class InvalidRefreshTokenException extends BaseException {

    private static final String CODE = "invalid.refresh.token.exception";

    public InvalidRefreshTokenException() {
        super(CODE);
    }
}
