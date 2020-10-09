package com.beeswork.balanceaccountservice.vm.match;


import com.beeswork.balanceaccountservice.validator.ValidUUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnmatchVM {

    @ValidUUID
    private String unmatcherId;

    @ValidUUID
    private String unmatchedId;

    private String unmatcherEmail;
}
