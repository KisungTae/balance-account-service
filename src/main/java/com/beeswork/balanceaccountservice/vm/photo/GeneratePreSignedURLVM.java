package com.beeswork.balanceaccountservice.vm.photo;

import com.beeswork.balanceaccountservice.vm.account.AccountIdentityVM;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class GeneratePreSignedURLVM extends AccountIdentityVM {
    private String photoKey;
}
