package com.beeswork.balanceaccountservice.dto.recommend;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecommendDTO {

    private String accountId;
    private String email;
    private int distance;
    private int minAge;
    private int maxAge;
    private boolean gender;
    private double latitude;
    private double longitude;
}
