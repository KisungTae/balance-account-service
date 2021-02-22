package com.beeswork.balanceaccountservice.dto.swipe;


import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class SwipeDTO {
    private UUID    swiperId;
    private UUID    swipedId;
    private String  photoKey;
    private Boolean deleted;
    private Date    updatedAt;

    @QueryProjection
    public SwipeDTO(UUID swipedId, Date updatedAt) {
        this.swipedId = swipedId;
        this.updatedAt = updatedAt;
    }

    @QueryProjection
    public SwipeDTO(UUID swiperId, String photoKey, Boolean deleted, Date updatedAt) {
        this.swiperId = swiperId;
        this.photoKey = photoKey;
        this.deleted = deleted;
        this.updatedAt = updatedAt;
    }
}
