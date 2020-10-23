package com.beeswork.balanceaccountservice.vm.account;

import com.beeswork.balanceaccountservice.validator.ValidUUID;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class AccountLocationSaveVM extends AccountIdentityVM {

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;
}
