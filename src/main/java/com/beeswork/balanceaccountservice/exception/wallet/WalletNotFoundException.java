package com.beeswork.balanceaccountservice.exception.wallet;

import com.beeswork.balanceaccountservice.exception.BaseException;

public class WalletNotFoundException extends BaseException {

    private static final String CODE = "wallet.not.found.exception";

    public WalletNotFoundException() {
        super(CODE);
    }
}
