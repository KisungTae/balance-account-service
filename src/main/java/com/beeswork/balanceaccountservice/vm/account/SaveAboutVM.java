package com.beeswork.balanceaccountservice.vm.account;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveAboutVM extends AccountIdentityVM {

    private String about;
    private Integer height;

}
