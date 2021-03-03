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
    private UUID swiperId;
    private UUID swipedId;
    private String repPhotoKey;
    private Boolean deleted;
    private Date updatedAt;

    @QueryProjection
    public SwipeDTO(UUID swipedId, Date updatedAt) {
        this.swipedId = swipedId;
        this.updatedAt = updatedAt;
    }

    @QueryProjection
    public SwipeDTO(UUID swiperId, String repPhotoKey, Boolean deleted, Date updatedAt) {
        this.swiperId = swiperId;
        this.repPhotoKey = repPhotoKey;
        this.deleted = deleted;
        this.updatedAt = updatedAt;
    }
}
