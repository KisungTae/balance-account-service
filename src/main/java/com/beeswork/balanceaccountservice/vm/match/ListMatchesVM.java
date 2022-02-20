package com.beeswork.balanceaccountservice.vm.match;

import com.beeswork.balanceaccountservice.constant.MatchPageFilter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class ListMatchesVM {

    @NotNull
    private Integer startPosition;

    @NotNull
    private Integer loadSize;

    private MatchPageFilter matchPageFilter;
}
