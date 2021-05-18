package com.beeswork.balanceaccountservice.dto.profile;

import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RecommendDTO {
    private List<CardDTO> cardDTOs;
    private boolean reset;
}
