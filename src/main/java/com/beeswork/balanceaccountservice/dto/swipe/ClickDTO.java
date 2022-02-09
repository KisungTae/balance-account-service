package com.beeswork.balanceaccountservice.dto.swipe;

import com.beeswork.balanceaccountservice.dto.common.Pushable;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClickDTO {
    private MatchDTO matchDTO;
    private Pushable pushable;
}
