package com.beeswork.balanceaccountservice.vm.profile;

import com.beeswork.balanceaccountservice.validator.ValidUUID;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class GetCardVM {

    @ValidUUID
    private UUID swipedId;
}
