package com.beeswork.balanceaccountservice.exception.match;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class MatchUnmatchedException extends BaseException {

    public static final String CODE = "match.unmatched.exception";

    public MatchUnmatchedException() {
        super(CODE);
    }
}
