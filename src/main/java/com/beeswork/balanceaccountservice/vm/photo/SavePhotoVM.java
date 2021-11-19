package com.beeswork.balanceaccountservice.vm.photo;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class SavePhotoVM {

    @NotEmpty
    private String photoKey;

    @NotNull
    private Integer sequence;
}
