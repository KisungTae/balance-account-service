package com.beeswork.balanceaccountservice.vm.account;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class SaveAboutVM extends AccountIdentityVM {

    @Length(max = 500, message = "{about.length}")
    private String about;

    private Integer height;

}
