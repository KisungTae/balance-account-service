package com.beeswork.balanceaccountservice.vm.swipe;


import com.beeswork.balanceaccountservice.validator.ValidUUID;
import com.beeswork.balanceaccountservice.vm.account.AccountIdentityVM;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.UUID;

@Getter
@Setter
public class LikeVM extends AccountIdentityVM {

    @ValidUUID
    private UUID swipedId;

}
