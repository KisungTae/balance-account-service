package com.beeswork.balanceaccountservice.exception.swipe;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class SwipeMatchedExistsException extends BaseException {
    public static final String SWIPE_MATCHED_EXISTS_EXCEPTION = "swipe.matched.exists.exception";
    public SwipeMatchedExistsException() {
        super(SWIPE_MATCHED_EXISTS_EXCEPTION);
    }
}
