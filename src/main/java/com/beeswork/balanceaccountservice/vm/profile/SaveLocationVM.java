package com.beeswork.balanceaccountservice.vm.profile;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.*;
import java.util.Date;

@Getter
@Setter
public class SaveLocationVM {

    @NotNull
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private Double latitude;

    @NotNull
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private Double longitude;

    @NotNull
    private Date updatedAt;

}
