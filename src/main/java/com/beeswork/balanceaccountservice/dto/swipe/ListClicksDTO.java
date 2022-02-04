package com.beeswork.balanceaccountservice.dto.swipe;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ListClicksDTO {
    private List<SwipeDTO> clickDTOs        = new ArrayList<>();
    private List<UUID>     deletedSwiperIds = new ArrayList<>();
}
