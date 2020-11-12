package com.beeswork.balanceaccountservice.vm.account;

import com.beeswork.balanceaccountservice.validator.ValidUUID;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.RegEx;
import javax.validation.constraints.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class SaveLocationVM extends AccountIdentityVM {

    @NotNull
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private Double latitude;

    @NotNull
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private Double longitude;

    @NotNull
    private Date locationUpdatedAt;

}
