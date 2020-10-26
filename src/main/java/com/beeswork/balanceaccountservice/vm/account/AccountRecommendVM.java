package com.beeswork.balanceaccountservice.vm.account;

import com.beeswork.balanceaccountservice.validator.ValidUUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.*;


@Getter
@Setter
public class AccountRecommendVM extends AccountIdentityVM {

    @Min(1000)
    @Max(10000)
    private int distance;

    private int minAge;
    private int maxAge;
    private boolean gender;
    private double latitude;
    private double longitude;
}
