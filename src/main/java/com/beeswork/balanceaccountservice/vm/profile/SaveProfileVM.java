package com.beeswork.balanceaccountservice.vm.profile;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;


@Getter
@Setter
public class SaveProfileVM {

//  TEST 1. when a value is invalid for multiple validator, then the last one win
    @NotEmpty(message = "{name.empty}")
    @Length(min = 1, max = 15, message = "{name.length}")
    private String name;

    @NotNull(message = "{birth.null}")
    private Date birth;

    @NotNull(message = "{gender.null}")
    private Boolean gender;

    @Length(max = 500, message = "{about.length}")
    private String about;

    private int height;

}
