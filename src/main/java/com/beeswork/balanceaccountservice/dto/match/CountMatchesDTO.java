package com.beeswork.balanceaccountservice.dto.match;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CountMatchesDTO {
    private long count;
    private Date countedAt;
}
