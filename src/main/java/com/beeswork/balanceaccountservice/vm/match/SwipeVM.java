package com.beeswork.balanceaccountservice.vm.match;


import com.beeswork.balanceaccountservice.validator.ValidUUID;
import lombok.Getter;
import lombok.Setter;

import java.util.Locale;

@Getter
@Setter
public class SwipeVM {

    private Long swipeId;

    @ValidUUID
    private String swiperId;

    @ValidUUID
    private String swipedId;

    private String swiperEmail;
}
