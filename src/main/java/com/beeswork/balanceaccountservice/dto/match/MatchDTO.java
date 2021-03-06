package com.beeswork.balanceaccountservice.dto.match;


import com.beeswork.balanceaccountservice.constant.PushType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class MatchDTO {
    private PushType pushType;
    private Long     chatId;
    private UUID    swiperId;
    private UUID    swipedId;
    private Boolean active;
    private Boolean unmatched;
    private String name;
    private String profilePhotoKey;
    private Boolean deleted;
    private Date createdAt;
    private Date updatedAt;

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

    public void swapIds() {
        UUID swiperId = this.swiperId;
        this.swiperId = this.swipedId;
        this.swipedId = swiperId;
    }
}
