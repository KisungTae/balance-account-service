package com.beeswork.balanceaccountservice.exception.jwt;

import com.beeswork.balanceaccountservice.exception.BaseException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpiredJWTException extends ExpiredJwtException {

    private String exceptionCode = "expired.jwt.exception";

    public ExpiredJWTException() {
        super(null, null, null);
    }

    public ExpiredJWTException(ExpiredJwtException expiredJwtException) {
        super(expiredJwtException.getHeader(), expiredJwtException.getClaims(), null);
    }
}
