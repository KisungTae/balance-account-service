package com.beeswork.balanceaccountservice.vm.profile;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.*;


@Getter
@Setter
public class SaveProfileVM {

//  TEST 1. when a value is invalid for multiple validator, then the last one win
    @NotEmpty(message = "{name.empty}")
    @Length(min = 1, max = 15, message = "{name.length}")
    private String name;

    @NotNull(message = "{gender.null}")
    private Boolean gender;

    @NotNull(message = "{birthDate.null}")
    private LocalDate birthDate;

    private Integer height;

    @Length(max = 500, message = "{about.length}")
    private String about;

    @NotNull
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private Double latitude;

    @NotNull
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private Double longitude;



}
