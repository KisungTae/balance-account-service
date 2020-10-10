package com.beeswork.balanceaccountservice.dto.firebase;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FirebaseTokenDTO {

    private String accountId;
    private String email;
    private String token;
}
