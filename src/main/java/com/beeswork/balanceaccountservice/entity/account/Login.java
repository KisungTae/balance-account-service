package com.beeswork.balanceaccountservice.entity.account;

import com.beeswork.balanceaccountservice.constant.LoginType;


public class Login {
    private String    id;
    private LoginType type;
    private Account   account;
    private String email;
    private boolean deleted;
    private boolean blocked;

}
