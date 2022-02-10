package com.beeswork.balanceaccountservice.dto.swipe;

import com.beeswork.balanceaccountservice.constant.ClickResult;
import com.beeswork.balanceaccountservice.entity.match.Match;
import com.beeswork.balanceaccountservice.entity.swipe.Swipe;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ClickTransactionResult {
    private ClickResult clickResult;
    private Swipe swipe;
    private Match match;
}
