package com.beeswork.balanceaccountservice.projection;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
public class ClickedProjection {

    private UUID clickerId;
    private String photoKey;

    @QueryProjection
    public ClickedProjection(UUID clickerId, String photoKey) {
        this.clickerId = clickerId;
        this.photoKey = photoKey;
    }
}
