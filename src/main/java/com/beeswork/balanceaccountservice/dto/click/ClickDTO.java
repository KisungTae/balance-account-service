package com.beeswork.balanceaccountservice.dto.click;

import com.beeswork.balanceaccountservice.validator.ValidUUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClickDTO {

    private Long swipeId;
    private String swiperId;
    private String swipedId;
    private String swiperEmail;
}
