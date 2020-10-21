package com.beeswork.balanceaccountservice.vm.recommend;

import com.beeswork.balanceaccountservice.validator.ValidUUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.*;


@Getter
@Setter
public class RecommendVM {

    @NotEmpty
    @ValidUUID
    private String accountId;

    @NotEmpty
    @Email
    private String email;

//    @Size(min = 1000, max = 10000)
    private int distance;

    private int minAge;
    private int maxAge;
    private boolean gender;
    private double latitude;
    private double longitude;
}
