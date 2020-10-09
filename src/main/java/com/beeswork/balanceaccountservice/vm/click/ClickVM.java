package com.beeswork.balanceaccountservice.vm.click;

import com.beeswork.balanceaccountservice.validator.ValidUUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
public class ClickVM {

    private Long swipeId;

    @ValidUUID
    private String swiperId;

    @ValidUUID
    private String swipedId;

    private String swiperEmail;
}
