package com.beeswork.balanceaccountservice.exception.swipe;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class SwipeNotFoundException extends BaseException {

    private static final String CODE = "swipe.not.found.exception";

    public SwipeNotFoundException() {
        super(CODE);
    }
}
