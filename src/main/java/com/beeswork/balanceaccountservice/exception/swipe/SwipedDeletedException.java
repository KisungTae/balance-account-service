package com.beeswork.balanceaccountservice.exception.swipe;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class SwipedDeletedException extends BaseException {

    private static final String CODE = "swiped.deleted.exception";
    public SwipedDeletedException() {
        super(CODE);
    }
}
