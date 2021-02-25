package com.beeswork.balanceaccountservice.entity.account;

import com.beeswork.balanceaccountservice.constant.SingleSignOnType;


public class SingleSignOn {
    private String id;
    private SingleSignOnType type;
    private Account account;
    private String email;
    private boolean deleted;
    private boolean blocked;

}
