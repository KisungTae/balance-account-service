package com.beeswork.balanceaccountservice.dto.match;


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
public class MatchDTO implements Pushable {

    @JsonIgnore
    private static final String PUSH_TITLE_MATCH = "push.title.match";

    @JsonIgnore
    private static final String PUSH_BODY_MATCH  = "push.body.match";

    private Long    chatId;
    private UUID    swiperId;
    private UUID    swipedId;
    private Boolean active;
    private Boolean unmatched;
    private String  name;
    private String  profilePhotoKey;
    private Boolean deleted;

    @QueryProjection
    public MatchDTO(Long chatId,
                    UUID swipedId,
                    boolean unmatched,
                    String name,
                    String profilePhotoKey,
                    boolean deleted,
                    boolean active) {
        this.chatId = chatId;
        this.swipedId = swipedId;
        this.unmatched = unmatched;
        this.name = name;
        this.profilePhotoKey = profilePhotoKey;
        this.deleted = deleted;
        this.active = active;
    }

    @Override
    public PushType getPushType() {
        return PushType.MATCH;
    }

    @Override
    @JsonIgnore
    public UUID getRecipientId() {
        return swiperId;
    }

    @Override
    @JsonIgnore
    public String[] getPushTitleArguments() {
        return null;
    }

    @Override
    @JsonIgnore
    public String[] getPushBodyArguments() {
        return new String[]{name};
    }

    @Override
    @JsonIgnore
    public String getPushTitleId() {
        return PUSH_TITLE_MATCH;
    }

    @Override
    @JsonIgnore
    public String getPushBodyId() {
        return PUSH_BODY_MATCH;
    }
}
