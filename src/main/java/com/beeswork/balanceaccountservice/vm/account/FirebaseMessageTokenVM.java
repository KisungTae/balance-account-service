package com.beeswork.balanceaccountservice.vm.account;


import com.beeswork.balanceaccountservice.validator.ValidUUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FirebaseMessageTokenVM {

    @ValidUUID
    private String accountId;

    private String email;
    private String token;
}
