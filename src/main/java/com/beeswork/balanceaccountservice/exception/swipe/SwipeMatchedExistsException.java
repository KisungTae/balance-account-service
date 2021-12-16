package com.beeswork.balanceaccountservice.exception.swipe;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class SwipeMatchedExistsException extends BaseException {
    public static final String CODE = "swipe.matched.exists.exception";
    public SwipeMatchedExistsException() {
        super(CODE);
    }
}
