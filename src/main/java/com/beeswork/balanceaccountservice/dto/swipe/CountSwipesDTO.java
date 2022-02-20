package com.beeswork.balanceaccountservice.dto.swipe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CountSwipesDTO {
    private long count;
    private Date fetchedAt;
}
