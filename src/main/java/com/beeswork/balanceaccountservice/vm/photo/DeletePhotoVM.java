package com.beeswork.balanceaccountservice.vm.photo;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class DeletePhotoVM {

    @NotEmpty
    private String photoKey;
}
