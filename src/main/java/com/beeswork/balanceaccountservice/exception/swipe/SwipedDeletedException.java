package com.beeswork.balanceaccountservice.exception.swipe;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class SwipedDeletedException extends BaseException {

    private static final String SWIPED_DELETED_EXCEPTION = "swiped.deleted.exception";
    public SwipedDeletedException() {
        super(SWIPED_DELETED_EXCEPTION);
    }
}
