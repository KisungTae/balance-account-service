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

    private static final String PUSH_TITLE_SWIPE = "push.title.swipe";
    private static final String PUSH_BODY_SWIPE  = "push.body.swipe";

    private static final String PUSH_TITLE_CLICK = "push.title.click";
    private static final String PUSH_BODY_CLICK  = "push.body.click";

    private PushType pushType = PushType.SWIPE;
    private Long     id;
    private UUID     swiperId;
    private UUID     swipedId;
    private String   profilePhotoKey;
    private Boolean  clicked;
    private Boolean  deleted;

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
    public String[] getPushTitleArguments() {
        return null;
    }

    @Override
    public String[] getPushBodyArguments() {
        return new String[0];
    }

    @Override
    public String getPushTitleId() {
        if (clicked) {
            return PUSH_TITLE_CLICK;
        }
        return PUSH_TITLE_SWIPE;
    }

    @Override
    public String getPushBodyId() {
        if (clicked) {
            return PUSH_BODY_CLICK;
        }
        return PUSH_BODY_SWIPE;
    }
}
