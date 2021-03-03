package com.beeswork.balanceaccountservice.vm.swipe;

import com.beeswork.balanceaccountservice.vm.account.AccountIdentityVM;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
public class ListSwipesVM extends AccountIdentityVM {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date fetchedAt = new Date();

    @NotNull
    private Boolean clicked;
}
