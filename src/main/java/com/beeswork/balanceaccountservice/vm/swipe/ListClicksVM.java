package com.beeswork.balanceaccountservice.vm.swipe;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ListClicksVM {
    @NotNull
    private Integer startPosition;

    @NotNull
    private Integer loadSize;
}
