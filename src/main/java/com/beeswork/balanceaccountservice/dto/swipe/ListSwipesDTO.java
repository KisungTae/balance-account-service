package com.beeswork.balanceaccountservice.dto.swipe;

import com.beeswork.balanceaccountservice.config.DatabaseConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ListSwipesDTO {
    private List<SwipeDTO> swipeDTOs;
    private Date fetchedAt;

    public ListSwipesDTO(Date fetchedAt) {
        this.fetchedAt = fetchedAt;
    }
}
