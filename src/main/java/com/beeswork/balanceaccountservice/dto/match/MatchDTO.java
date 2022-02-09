package com.beeswork.balanceaccountservice.dto.match;


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
public class MatchDTO implements Pushable {

    private static final String        PUSH_TITLE_MATCH        = "push.title.match";
    private static final String        PUSH_BODY_MATCH         = "push.body.match";

    private PushType pushType = PushType.MATCH;
    private Long     chatId;
    private UUID     swiperId;
    private UUID     swipedId;
    private Boolean  active;
    private Boolean  unmatched;
    private String   name;
    private String   profilePhotoKey;
    private Boolean  deleted;
    private Date     createdAt;
    private Date     updatedAt;

    public MatchDTO(PushType pushType) {
        this.pushType = pushType;
    }

    @QueryProjection
    public MatchDTO(Long chatId,
                    UUID swipedId,
                    boolean unmatched,
                    String name,
                    String profilePhotoKey,
                    boolean deleted,
                    boolean active,
                    Date createdAt,
                    Date updatedAt) {
        this.chatId = chatId;
        this.swipedId = swipedId;
        this.unmatched = unmatched;
        this.name = name;
        this.profilePhotoKey = profilePhotoKey;
        this.deleted = deleted;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Override
    public UUID getRecipientId() {
        return swiperId;
    }

    @Override
    public String[] getPushTitleArguments() {
        return null;
    }

    @Override
    public String[] getPushBodyArguments() {
        return new String[] {name};
    }

    @Override
    public String getPushTitleId() {
        return PUSH_TITLE_MATCH;
    }

    @Override
    public String getPushBodyId() {
        return PUSH_BODY_MATCH;
    }
}
