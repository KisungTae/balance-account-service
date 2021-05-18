package com.beeswork.balanceaccountservice.vm.profile;

import com.beeswork.balanceaccountservice.constant.Gender;
import com.beeswork.balanceaccountservice.vm.account.AccountIdentityVM;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.util.Date;


@Getter
@Setter
public class RecommendVM extends AccountIdentityVM {

    private int     distance;
    private int     minAge;
    private int    maxAge;
    private boolean gender;
    private int    pageIndex;
}
