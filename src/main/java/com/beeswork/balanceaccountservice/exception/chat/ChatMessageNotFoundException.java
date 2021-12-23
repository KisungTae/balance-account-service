package com.beeswork.balanceaccountservice.exception.chat;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class ChatMessageNotFoundException extends BaseException {

    private static final String CODE = "chat.message.not.found.exception";

    public ChatMessageNotFoundException() {
        super(CODE);
    }
}
