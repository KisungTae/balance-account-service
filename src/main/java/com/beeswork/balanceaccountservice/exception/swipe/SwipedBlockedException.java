package com.beeswork.balanceaccountservice.exception.swipe;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class SwipedBlockedException extends BaseException {

    private static final String CODE = "swiped.blocked.exception";

    public SwipedBlockedException() {
        super(CODE);
    }
}
