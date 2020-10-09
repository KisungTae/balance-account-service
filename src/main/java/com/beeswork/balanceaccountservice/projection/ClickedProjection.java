package com.beeswork.balanceaccountservice.projection;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;


@Getter
@Setter
public class ClickedProjection {

    private UUID swiperId;
    private String photoKey;

    @QueryProjection
    public ClickedProjection(UUID swiperId, String photoKey) {
        this.swiperId = swiperId;
        this.photoKey = photoKey;
    }
}
