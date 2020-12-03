package com.beeswork.balanceaccountservice.vm.swipe;


import com.beeswork.balanceaccountservice.validator.ValidUUID;
import com.beeswork.balanceaccountservice.vm.account.AccountIdentityVM;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Locale;

@Getter
@Setter
public class SwipeVM extends AccountIdentityVM {

    @NotEmpty(message = "{uuid.empty}")
    @ValidUUID(message = "{uuid.invalid}")
    private String swipedId;

}
