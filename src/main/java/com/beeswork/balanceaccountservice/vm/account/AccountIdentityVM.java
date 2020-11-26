package com.beeswork.balanceaccountservice.vm.account;

import com.beeswork.balanceaccountservice.validator.ValidUUID;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class AccountIdentityVM {

    @NotEmpty(message = "{account.uuid.empty}")
    @ValidUUID(message = "{account.uuid.invalid}")
    protected String accountId;

    @NotEmpty(message = "{identity.token.uuid.empty}")
    @ValidUUID(message = "{identity.token.uuid.invalid}")
    protected String identityToken;
}
