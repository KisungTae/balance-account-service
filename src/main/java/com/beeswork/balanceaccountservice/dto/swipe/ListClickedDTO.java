package com.beeswork.balanceaccountservice.dto.swipe;

import com.beeswork.balanceaccountservice.config.DatabaseConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ListClickedDTO {
    private List<SwipeDTO> swipeDTOs;
    private Date fetchedAt;

    public ListClickedDTO(List<SwipeDTO> swipeDTOs, Date fetchedAt) {
        this.swipeDTOs = swipeDTOs;
        this.fetchedAt = fetchedAt;
    }
}

