package com.beeswork.balanceaccountservice.vm.swipe;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class FetchClicksVM {

    private UUID lastSwiperId;

    @NotNull
    private Integer loadSize;
}
