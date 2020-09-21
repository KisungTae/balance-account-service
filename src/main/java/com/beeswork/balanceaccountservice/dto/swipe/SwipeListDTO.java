package com.beeswork.balanceaccountservice.dto.swipe;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SwipeListDTO {

    private String urlPrefix;
    private List<String> swiperIds = new ArrayList<>();
}
