package com.beeswork.balanceaccountservice.dto.swipe;

import com.beeswork.balanceaccountservice.constant.ClickResult;
import com.beeswork.balanceaccountservice.dto.common.Pushable;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClickDTO {
    private ClickResult clickResult;
    private MatchDTO matchDTO;
}
