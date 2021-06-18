package com.beeswork.balanceaccountservice.vm.profile;

import com.beeswork.balanceaccountservice.vm.account.AccountIdentityVM;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class SaveAboutVM extends AccountIdentityVM {

    @Length(max = 5, message = "{about.length}")
    private String about;

    private Integer height;

}
