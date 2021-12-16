package com.beeswork.balanceaccountservice.exception.jwt;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class ExpiredJWTTokenException extends BaseException {
    private static final String CODE = "expired.jwt.token.exception";

    public ExpiredJWTTokenException() {
        super(CODE);
    }
}
