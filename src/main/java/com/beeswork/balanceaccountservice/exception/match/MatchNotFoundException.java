package com.beeswork.balanceaccountservice.exception.match;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class MatchNotFoundException extends BaseException {
    public static final String CODE = "match.not.found.exception";

    public MatchNotFoundException() {
        super(CODE);
    }
}
