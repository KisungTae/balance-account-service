package com.beeswork.balanceaccountservice.vm.profile;

import com.beeswork.balanceaccountservice.validator.ValidUUID;
import com.beeswork.balanceaccountservice.vm.account.AccountIdentityVM;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Getter
@Setter
public class GetCardVM extends AccountIdentityVM {

    @ValidUUID
    private UUID swipedId;
}
