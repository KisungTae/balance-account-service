package com.beeswork.balanceaccountservice.projection;

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
public class MatchProjection {
    private Long    chatId;
    private UUID    matchedId;
    private Date    matchUpdatedAt;
    private boolean unmatched;
    private String  name;
    private String  repPhotoKey;
    private boolean blocked;
    private boolean deleted;
    private Date    accountUpdatedAt;

    @QueryProjection
    public MatchProjection(Long chatId,
                           UUID matchedId,
                           Date matchUpdatedAt,
                           boolean unmatched,
                           String name,
                           String repPhotoKey,
                           boolean blocked,
                           boolean deleted,
                           Date accountUpdatedAt) {
        this.chatId = chatId;
        this.matchedId = matchedId;
        this.matchUpdatedAt = matchUpdatedAt;
        this.unmatched = unmatched;
        this.name = name;
        this.repPhotoKey = repPhotoKey;
        this.blocked = blocked;
        this.deleted = deleted;
        this.accountUpdatedAt = accountUpdatedAt;
    }

    public MatchProjection(Long chatId, UUID matchedId, String name, String repPhotoKey, Date matchUpdatedAt) {
        this.chatId = chatId;
        this.matchedId = matchedId;
        this.name = name;
        this.repPhotoKey = repPhotoKey;
        this.matchUpdatedAt = matchUpdatedAt;
    }

    public MatchProjection(UUID matchedId, Date matchUpdatedAt) {
        this.matchedId = matchedId;
        this.matchUpdatedAt = matchUpdatedAt;
    }

}
