package com.beeswork.balanceaccountservice.exception.swipe;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class SwipedDeletedException extends BaseException {

    public static final String CODE = "swiped.deleted.exception";
    public SwipedDeletedException() {
        super(CODE);
    }
}
