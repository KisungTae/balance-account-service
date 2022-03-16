package com.beeswork.balanceaccountservice.vm.swipe;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class FetchSwipesVM {

    private Long lastSwipeId;

    @NotNull
    private Integer loadSize;
}
