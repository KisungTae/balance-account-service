package com.beeswork.balanceaccountservice.vm.match;

import com.beeswork.balanceaccountservice.vm.account.AccountIdentityVM;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class ListMatchesVM extends AccountIdentityVM {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date matchFetchedAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date accountFetchedAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date chatMessageFetchedAt;
}
