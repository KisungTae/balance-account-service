package com.beeswork.balanceaccountservice.dto.count;

import com.beeswork.balanceaccountservice.constant.TabPosition;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CountTabDTO {

    private long        count;
    private Date        countedAt;
    private TabPosition tabPosition;
}
