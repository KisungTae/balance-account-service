package com.beeswork.balanceaccountservice.exception.swipe;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class SwipeClickedExistsException extends BaseException {

    public static final String CODE = "swipe.clicked.exists.exception";

    public SwipeClickedExistsException() {
        super(CODE);
    }
}
