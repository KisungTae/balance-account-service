package com.beeswork.balanceaccountservice.dto.swipe;


import com.beeswork.balanceaccountservice.constant.PushType;
import com.beeswork.balanceaccountservice.dto.common.Pushable;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class SwipeDTO implements Pushable {
    private PushType pushType = PushType.SWIPE;
    private Long    id;
    private UUID    swiperId;
    private UUID    swipedId;
    private String  profilePhotoKey;
    private Boolean clicked;
    private Boolean deleted;

    @QueryProjection
    public SwipeDTO(Long id, UUID swiperId, UUID swipedId, String profilePhotoKey, Boolean clicked) {
        this.id = id;
        this.swiperId = swiperId;
        this.swipedId = swipedId;
        this.profilePhotoKey = profilePhotoKey;
        this.clicked = clicked;
    }

    @QueryProjection
    public SwipeDTO(Long id, UUID swiperId, UUID swipedId, String profilePhotoKey, Boolean clicked, Boolean deleted) {
        this.id = id;
        this.swiperId = swiperId;
        this.swipedId = swipedId;
        this.profilePhotoKey = profilePhotoKey;
        this.clicked = clicked;
        this.deleted = deleted;
    }

    @Override
    public UUID getRecipientId() {
        return swipedId;
    }

    @Override
    public PushType getPushType() {
        return pushType;
    }
}
