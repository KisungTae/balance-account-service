package com.beeswork.balanceaccountservice.dto.account;

import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;


@Getter
@Setter
public class PreRecommendDTO {
    private Point location;
    private int   index;
}
