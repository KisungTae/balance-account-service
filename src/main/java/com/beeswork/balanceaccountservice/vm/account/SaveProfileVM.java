package com.beeswork.balanceaccountservice.vm.account;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;


@Getter
@Setter
public class SaveProfileVM extends AccountIdentityVM {


//  TEST 1. when a value is invalid for multiple validator, then the last one win
    @NotEmpty(message = "{name.empty}")
    @Length(min = 1, max = 50, message = "{name.length}")
    private String name;

    @NotNull(message = "{birth.null}")
    private Date birth;

    @NotNull(message = "{gender.null}")
    private Boolean gender;

    private String about;
    private Integer height;

}
