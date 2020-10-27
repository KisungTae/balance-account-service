package com.beeswork.balanceaccountservice.vm.match;


import com.beeswork.balanceaccountservice.validator.ValidUUID;
import com.beeswork.balanceaccountservice.vm.account.AccountIdentityVM;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class UnmatchVM extends AccountIdentityVM {

    @NotEmpty(message = "{uuid.empty}")
    @ValidUUID(message = "{uuid.invalid}")
    private String unmatchedId;

}
