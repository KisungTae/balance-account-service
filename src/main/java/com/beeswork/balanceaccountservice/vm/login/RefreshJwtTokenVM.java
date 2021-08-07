package com.beeswork.balanceaccountservice.vm.login;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshJwtTokenVM {
    private String jwtToken;
    private String refreshToken;

}
