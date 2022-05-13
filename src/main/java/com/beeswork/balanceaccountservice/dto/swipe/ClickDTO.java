package com.beeswork.balanceaccountservice.dto.swipe;

import com.beeswork.balanceaccountservice.constant.ClickOutcome;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClickDTO {
    private ClickOutcome clickOutcome;
    private int          point;
    private MatchDTO     matchDTO;
}
