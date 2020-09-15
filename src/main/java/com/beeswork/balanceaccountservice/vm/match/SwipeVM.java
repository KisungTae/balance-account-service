package com.beeswork.balanceaccountservice.vm.match;


import com.beeswork.balanceaccountservice.validator.ValidUUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SwipeVM {

    @ValidUUID
    private String swiperId;

    @ValidUUID
    private String swipedId;

    private String swiperEmail;
}
