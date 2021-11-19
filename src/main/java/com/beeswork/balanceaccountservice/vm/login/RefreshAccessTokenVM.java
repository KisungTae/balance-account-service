package com.beeswork.balanceaccountservice.vm.login;

import com.beeswork.balanceaccountservice.validator.ValidUUID;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.UUID;

@Getter
@Setter
public class RefreshAccessTokenVM {

    @NotEmpty
    private String refreshToken;
}
