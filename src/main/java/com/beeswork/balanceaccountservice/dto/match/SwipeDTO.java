package com.beeswork.balanceaccountservice.dto.match;

import com.beeswork.balanceaccountservice.validator.ValidUUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SwipeDTO {

    private String swiperId;
    private String swipedId;
    private String swiperEmail;
}
