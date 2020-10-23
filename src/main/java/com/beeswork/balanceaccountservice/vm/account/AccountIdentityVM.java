package com.beeswork.balanceaccountservice.vm.account;

import com.beeswork.balanceaccountservice.validator.ValidUUID;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class AccountIdentityVM {

    @NotEmpty(message = "{uuid.empty}")
    @ValidUUID(message = "{uuid.invalid}")
    protected String accountId;

    @NotEmpty(message = "{email.empty}")
    @Email(message = "{email.invalid}")
    protected String email;
}
