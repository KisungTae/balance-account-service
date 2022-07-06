package com.beeswork.balanceaccountservice.dto.common;

import lombok.AllArgsConstructor;

import java.util.Date;

@AllArgsConstructor
public class CountPagingItemDTO {

    private long count;
    private Date countedAt;
}
