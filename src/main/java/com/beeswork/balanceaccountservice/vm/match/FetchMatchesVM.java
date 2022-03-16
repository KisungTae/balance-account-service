package com.beeswork.balanceaccountservice.vm.match;

import com.beeswork.balanceaccountservice.constant.MatchPageFilter;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class FetchMatchesVM {

    private Long lastMatchId;

    @NotNull
    private Integer loadSize;

    private MatchPageFilter matchPageFilter;
}
