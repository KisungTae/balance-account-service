package com.beeswork.balanceaccountservice.vm.account;

import com.beeswork.balanceaccountservice.validator.ValidUUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.bind.annotation.RequestParam;

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

    private Date locationUpdatedAt;
    private boolean reset;
}
