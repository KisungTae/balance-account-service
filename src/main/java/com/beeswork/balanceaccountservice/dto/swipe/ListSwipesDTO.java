package com.beeswork.balanceaccountservice.dto.swipe;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ListSwipesDTO {
    private List<SwipeDTO> swipeDTOs = new ArrayList<>();
    private long swipeCount;
    private Date swipeCountCountedAt;
}
