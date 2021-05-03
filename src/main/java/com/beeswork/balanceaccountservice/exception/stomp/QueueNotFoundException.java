package com.beeswork.balanceaccountservice.exception.stomp;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class QueueNotFoundException extends BaseException {

    private static final String QUEUE_NOT_FOUND_EXCEPTION = "queue.not.found.exception";

    public QueueNotFoundException() {
        super(QUEUE_NOT_FOUND_EXCEPTION);
    }
}
