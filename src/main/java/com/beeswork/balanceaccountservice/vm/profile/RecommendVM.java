package com.beeswork.balanceaccountservice.vm.profile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecommendVM {

    private int     distance;
    private int     minAge;
    private int    maxAge;
    private boolean gender;
    private int    pageIndex;
}
