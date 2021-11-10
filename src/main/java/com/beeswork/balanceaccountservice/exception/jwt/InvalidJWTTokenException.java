package com.beeswork.balanceaccountservice.exception.jwt;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class InvalidJWTTokenException extends BaseException {

    private static final String INVALID_JWT_TOKEN_EXCEPTION = "invalid.jwt.token.exception";

    public InvalidJWTTokenException() {
        super(INVALID_JWT_TOKEN_EXCEPTION);
    }
}
