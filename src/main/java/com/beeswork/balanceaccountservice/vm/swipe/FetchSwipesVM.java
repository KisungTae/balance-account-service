package com.beeswork.balanceaccountservice.vm.swipe;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class FetchSwipesVM {

    private UUID lastSwiperId;

    @NotNull
    private Integer loadSize;
}
