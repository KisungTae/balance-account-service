package com.beeswork.balanceaccountservice.vm.swipe;

import com.beeswork.balanceaccountservice.validator.ValidUUID;
import com.beeswork.balanceaccountservice.vm.account.AccountIdentityVM;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ClickVM extends AccountIdentityVM {

    @NotNull
    private Long swipeId;

    @NotEmpty(message = "{uuid.empty}")
    @ValidUUID(message = "{uuid.invalid}")
    private String swipedId;

    @Size(min = 1, max = 3, message = "{swipe.click.answers.size}")
    private Map<Long, Boolean> answers = new HashMap<>();
}
