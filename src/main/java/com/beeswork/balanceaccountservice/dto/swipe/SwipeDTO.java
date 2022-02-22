package com.beeswork.balanceaccountservice.dto.swipe;


import com.beeswork.balanceaccountservice.constant.PushType;
import com.beeswork.balanceaccountservice.dto.common.Pushable;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    private static final String PUSH_TITLE_SWIPE = "push.title.swipe";

    @JsonIgnore
    private static final String PUSH_BODY_SWIPE = "push.body.swipe";

    @JsonIgnore
    private static final String PUSH_TITLE_CLICK = "push.title.click";

    @JsonIgnore
    private static final String PUSH_BODY_CLICK = "push.body.click";

    private Long    id;
    private UUID    swiperId;
    private UUID    swipedId;
    private Boolean clicked;
    private Date    createdAt;
    private String  swiperProfilePhotoKey;
    private Boolean swiperDeleted;


    @QueryProjection
    public SwipeDTO(Long id, UUID swiperId, UUID swipedId, String swiperProfilePhotoKey, Boolean clicked) {
        this.id = id;
        this.swiperId = swiperId;
        this.swipedId = swipedId;
        this.swiperProfilePhotoKey = swiperProfilePhotoKey;
        this.clicked = clicked;
    }

    @QueryProjection
    public SwipeDTO(Long id, UUID swiperId, UUID swipedId, String swiperProfilePhotoKey, Boolean clicked, Boolean swiperDeleted) {
        this.id = id;
        this.swiperId = swiperId;
        this.swipedId = swipedId;
        this.swiperProfilePhotoKey = swiperProfilePhotoKey;
        this.clicked = clicked;
        this.swiperDeleted = swiperDeleted;
    }

    @Override
    @JsonIgnore
    public PushType getPushType() {
        return PushType.SWIPE;
    }

    @Override
    @JsonIgnore
    public UUID getRecipientId() {
        return swipedId;
    }

    @Override
    @JsonIgnore
    public String[] getPushTitleArguments() {
        return null;
    }

    @Override
    @JsonIgnore
    public String[] getPushBodyArguments() {
        return new String[0];
    }

    @Override
    @JsonIgnore
    public String getPushTitleId() {
        if (clicked) {
            return PUSH_TITLE_CLICK;
        }
        return PUSH_TITLE_SWIPE;
    }

    @Override
    @JsonIgnore
    public String getPushBodyId() {
        if (clicked) {
            return PUSH_BODY_CLICK;
        }
        return PUSH_BODY_SWIPE;
    }
}
