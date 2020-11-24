package com.beeswork.balanceaccountservice.projection;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class ClickProjection {

    private UUID swipedId;
    private Date updatedAt;

    @QueryProjection
    public ClickProjection(UUID swipedId, Date updatedAt) {
        this.swipedId = swipedId;
        this.updatedAt = updatedAt;
    }
}
