package com.beeswork.balanceaccountservice.vm.profile;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class SaveAboutVM {

    @Length(max = 50, message = "{about.length}")
    private String about;

    private Integer height;

}
