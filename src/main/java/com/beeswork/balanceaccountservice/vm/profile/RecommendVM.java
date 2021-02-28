package com.beeswork.balanceaccountservice.vm.profile;

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
    private int     maxAge;
    private boolean gender;

    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private Double latitude;

    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private Double longitude;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date locationUpdatedAt;

    private boolean reset;
}
