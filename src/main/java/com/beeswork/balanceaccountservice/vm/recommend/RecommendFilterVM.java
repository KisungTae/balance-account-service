package com.beeswork.balanceaccountservice.vm.recommend;

import com.beeswork.balanceaccountservice.validator.ValidUUID;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;

public class RecommendFilterVM implements Serializable {

    @ValidUUID
    private String accountId;

    @Min(1)
    @Max(10)
    private int distance;

    private int minAge;

    private int maxAge;

    private boolean showMe;

    private double latitude;

    private double longitude;
}
