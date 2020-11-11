package com.beeswork.balanceaccountservice.exception.swipe;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class SwipedNotFoundException extends BaseException {

    private static final String SWIPED_NOT_FOUND_EXCEPTION = "swiped.not.found.exception";

    public SwipedNotFoundException() {
        super(SWIPED_NOT_FOUND_EXCEPTION);
    }
}
