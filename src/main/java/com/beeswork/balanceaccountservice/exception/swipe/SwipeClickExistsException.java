package com.beeswork.balanceaccountservice.exception.swipe;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class SwipeClickExistsException extends BaseException {

    public static final String SWIPE_CLICKED_EXISTS_EXCEPTION = "swipe.clicked.exists.exception";

    public SwipeClickExistsException() {
        super(SWIPE_CLICKED_EXISTS_EXCEPTION);
    }
}
