package com.beeswork.balanceaccountservice.vm.login;

import com.beeswork.balanceaccountservice.vm.account.AccountIdentityVM;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class SaveEmailVM extends AccountIdentityVM {

    @NotEmpty(message = "{email.empty}")
    @Email(message = "{email.invalid}")
    private String email;
}
