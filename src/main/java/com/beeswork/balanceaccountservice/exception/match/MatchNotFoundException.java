package com.beeswork.balanceaccountservice.exception.match;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class MatchNotFoundException extends BaseException {

    private static final String MATCH_NOT_FOUND_EXCEPTION = "match.not.found.exception";

    public MatchNotFoundException() {
        super(MATCH_NOT_FOUND_EXCEPTION);
    }
}
