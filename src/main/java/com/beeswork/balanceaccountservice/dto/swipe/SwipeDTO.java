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
    private Long    id;
    private UUID    swiperId;
    private UUID    swipedId;
    private String  name;
    private String  profilePhotoKey;
    private Boolean clicked;
    private Boolean deleted;

    @QueryProjection
    public SwipeDTO(Long id, UUID swiperId, UUID swipedId, String name, String profilePhotoKey, Boolean clicked) {
        this.id = id;
        this.swiperId = swiperId;
        this.swipedId = swipedId;
        this.name = name;
        this.profilePhotoKey = profilePhotoKey;
        this.clicked = clicked;
    }

    @QueryProjection
    public SwipeDTO(Long id, UUID swiperId, UUID swipedId, String name, String profilePhotoKey, Boolean clicked, Boolean deleted) {
        this.id = id;
        this.swiperId = swiperId;
        this.swipedId = swipedId;
        this.name = name;
        this.profilePhotoKey = profilePhotoKey;
        this.clicked = clicked;
        this.deleted = deleted;
    }
}
