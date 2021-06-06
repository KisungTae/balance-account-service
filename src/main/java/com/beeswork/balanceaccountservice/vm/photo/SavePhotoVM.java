package com.beeswork.balanceaccountservice.vm.photo;

import com.beeswork.balanceaccountservice.vm.account.AccountIdentityVM;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class SavePhotoVM extends AccountIdentityVM {

    @NotEmpty
    private String photoKey;

    @NotNull
    private Integer sequence;
}
