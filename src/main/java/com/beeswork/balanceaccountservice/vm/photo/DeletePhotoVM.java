package com.beeswork.balanceaccountservice.vm.photo;

import com.beeswork.balanceaccountservice.vm.account.AccountIdentityVM;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class DeletePhotoVM extends AccountIdentityVM {

    @NotEmpty
    private String photoKey;
}
