package com.beeswork.balanceaccountservice.exception.match;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class MatchUnmatchedException extends BaseException {

    private static final String MATCH_UNMATCHED_EXCEPTION = "match.unmatched.exception";

    public MatchUnmatchedException() {
        super(MATCH_UNMATCHED_EXCEPTION);
    }
}
