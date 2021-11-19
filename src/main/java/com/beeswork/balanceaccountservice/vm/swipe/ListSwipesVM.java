package com.beeswork.balanceaccountservice.vm.swipe;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

@Getter
@Setter
public class ListSwipesVM  {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date fetchedAt = new Date();
}
