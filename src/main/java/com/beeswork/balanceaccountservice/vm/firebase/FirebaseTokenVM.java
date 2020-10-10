package com.beeswork.balanceaccountservice.vm.firebase;


import com.beeswork.balanceaccountservice.validator.ValidUUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FirebaseTokenVM {

    @ValidUUID
    private String accountId;

    private String email;
    private String token;
}
