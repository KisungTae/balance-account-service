package com.beeswork.balanceaccountservice.vm.photo;

import com.beeswork.balanceaccountservice.vm.account.AccountIdentityVM;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AddPhotoVM extends AccountIdentityVM {

    @NotEmpty
    private String photoKey;

    @NotNull
    private Integer sequence;
}
