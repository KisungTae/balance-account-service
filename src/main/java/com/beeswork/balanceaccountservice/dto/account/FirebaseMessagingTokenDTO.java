package com.beeswork.balanceaccountservice.dto.account;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FirebaseMessagingTokenDTO {

    private String accountId;
    private String email;
    private String token;
}
