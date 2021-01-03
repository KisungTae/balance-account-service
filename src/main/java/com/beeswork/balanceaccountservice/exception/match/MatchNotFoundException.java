package com.beeswork.balanceaccountservice.exception.match;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class MatchNotFoundException extends BaseException {
    public MatchNotFoundException() {
        super("match.not.found.exception");
    }
}
