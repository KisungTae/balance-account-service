package com.beeswork.balanceaccountservice.vm.match;


import com.beeswork.balanceaccountservice.validator.ValidUUID;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class UnmatchVM {

    @ValidUUID
    private UUID swipedId;

}
