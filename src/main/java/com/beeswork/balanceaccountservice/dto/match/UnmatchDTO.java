package com.beeswork.balanceaccountservice.dto.match;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class UnmatchDTO {
    private long matchCount;
    private Date matchCountCountedAt;
}
