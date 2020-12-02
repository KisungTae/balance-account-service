package com.beeswork.balanceaccountservice.projection;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.UUID;


@Getter
@Setter
public class ClickedProjection {

    private UUID   swiperId;
    private String photoKey;
    private boolean blocked;
    private boolean deleted;
    private Date updatedAt;

    @QueryProjection
    public ClickedProjection(UUID swiperId, String photoKey, boolean blocked, boolean deleted, Date updatedAt) {
        this.swiperId = swiperId;
        this.photoKey = photoKey;
        this.blocked = blocked;
        this.deleted = deleted;
        this.updatedAt = updatedAt;
    }
}
