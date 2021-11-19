package com.beeswork.balanceaccountservice.vm.match;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

@Getter
@Setter
public class ListMatchesVM {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date fetchedAt;
}
