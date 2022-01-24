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
    private String  name;
    private String  profilePhotoKey;
    private Boolean deleted;
    private Date    updatedAt;

    @QueryProjection
    public SwipeDTO(UUID swiperId, String name, String profilePhotoKey, Boolean deleted, Date updatedAt) {
        this.swiperId = swiperId;
        this.name = name;
        this.profilePhotoKey = profilePhotoKey;
        this.deleted = deleted;
        this.updatedAt = updatedAt;
    }
}
