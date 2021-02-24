package com.beeswork.balanceaccountservice.exception.swipe;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class SwipeClickedExistsException extends BaseException {

    public static final String SWIPE_CLICKED_EXISTS_EXCEPTION = "swipe.clicked.exists.exception";

    public SwipeClickedExistsException() {
        super(SWIPE_CLICKED_EXISTS_EXCEPTION);
    }
}
