package com.beeswork.balanceaccountservice.dto.match;

import com.beeswork.balanceaccountservice.validator.ValidUUID;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UnmatchDTO {

    private String unmatcherId;
    private String unmatchedId;
    private String unmatcherEmail;
}
