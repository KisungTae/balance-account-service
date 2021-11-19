package com.beeswork.balanceaccountservice.vm.swipe;

import com.beeswork.balanceaccountservice.validator.ValidUUID;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class ClickVM {

    @ValidUUID
    private UUID swipedId;

    @Size(min = 3, max = 6, message = "{swipe.click.answers.size}")
    private Map<Integer, Boolean> answers = new HashMap<>();
}
