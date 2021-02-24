package com.beeswork.balanceaccountservice.vm.swipe;

import com.beeswork.balanceaccountservice.vm.account.AccountIdentityVM;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
public class ListSwipesVM extends AccountIdentityVM {

    @NotNull
    private Date fetchedAt;

    @NotNull
    private Boolean clicked;
}
