package com.beeswork.balanceaccountservice.vm.account;

import com.beeswork.balanceaccountservice.validator.ValidUUID;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.UUID;

@Getter
@Setter
public class AccountIdentityVM {

    @ValidUUID
    protected UUID accountId;

    @ValidUUID
    protected UUID identityToken;
}
