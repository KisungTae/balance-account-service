package com.beeswork.balanceaccountservice.exception.swipe;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class SwipedBlockedException extends BaseException {

    private static final String SWIPED_BLOCKED_EXCEPTION = "swiped.blocked.exception";

    public SwipedBlockedException() {
        super(SWIPED_BLOCKED_EXCEPTION);
    }
}
