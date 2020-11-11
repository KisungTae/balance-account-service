package com.beeswork.balanceaccountservice.exception.swipe;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class SwipeNotFoundException extends BaseException {

    private static final String SWIPE_NOT_FOUND_EXCEPTION = "swipe.not.found.exception";

    public SwipeNotFoundException() {
        super(SWIPE_NOT_FOUND_EXCEPTION);
    }
}
