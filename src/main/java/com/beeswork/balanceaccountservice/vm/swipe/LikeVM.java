package com.beeswork.balanceaccountservice.vm.swipe;


import com.beeswork.balanceaccountservice.validator.ValidUUID;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class LikeVM {

    @ValidUUID
    private UUID swipedId;

}
