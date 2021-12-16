package com.beeswork.balanceaccountservice.exception.stomp;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class QueueNotFoundException extends BaseException {

    private static final String CODE = "queue.not.found.exception";

    public QueueNotFoundException() {
        super(CODE);
    }
}
