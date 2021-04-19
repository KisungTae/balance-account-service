package com.beeswork.balanceaccountservice.dto.match;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class MatchDTO {
    private Long chatId;
    private UUID matchedId;
    private boolean active;
    private boolean unmatched;
    private String name;
    private String repPhotoKey;

    @JsonIgnore
    private boolean deleted;

    private Date createdAt;
    private Date updatedAt;

    @QueryProjection
    public MatchDTO(Long chatId,
                    UUID matchedId,
                    boolean unmatched,
                    String name,
                    String repPhotoKey,
                    boolean deleted,
                    boolean active,
                    Date createdAt,
                    Date updatedAt) {
        this.chatId = chatId;
        this.matchedId = matchedId;
        this.unmatched = unmatched;
        this.name = name;
        this.repPhotoKey = repPhotoKey;
        this.deleted = deleted;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
