package com.beeswork.balanceaccountservice.dto.match;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class MatchDTO {
    private Long    chatId;
    private UUID    matchedId;
    private boolean unmatched;
    private Date    updatedAt;
    private String  name;
    private String  repPhotoKey;
    private boolean blocked;
    private boolean deleted;
    private Date    accountUpdatedAt;

    @QueryProjection
    public MatchDTO(Long chatId,
                    UUID matchedId,
                    boolean unmatched,
                    Date updatedAt,
                    String name,
                    String repPhotoKey,
                    boolean blocked,
                    boolean deleted,
                    Date accountUpdatedAt) {
        this.chatId = chatId;
        this.matchedId = matchedId;
        this.unmatched = unmatched;
        this.updatedAt = updatedAt;
        this.name = name;
        this.repPhotoKey = repPhotoKey;
        this.blocked = blocked;
        this.deleted = deleted;
        this.accountUpdatedAt = accountUpdatedAt;
    }
}
