package com.beeswork.balanceaccountservice.dto.swipe;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ListSwipesDTO {
    private List<SwipeDTO> swipeDTOs;
    private Date           fetchedAt;

    public ListSwipesDTO(List<SwipeDTO> swipeDTOs, Date fetchedAt) {
        this.swipeDTOs = swipeDTOs;
        this.fetchedAt = fetchedAt;
    }
}
