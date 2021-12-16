package com.beeswork.balanceaccountservice.exception.swipe;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class SwipedNotFoundException extends BaseException {

    private static final String CODE = "swiped.not.found.exception";

    public SwipedNotFoundException() {
        super(CODE);
    }
}
