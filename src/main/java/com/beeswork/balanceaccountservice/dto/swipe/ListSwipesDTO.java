package com.beeswork.balanceaccountservice.dto.swipe;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ListSwipesDTO {
    private List<UUID> swipedIds;
    private Date fetchedAt;

    public ListSwipesDTO(Date fetchedAt) {
        this.fetchedAt = fetchedAt;
    }
}
