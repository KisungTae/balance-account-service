package com.beeswork.balanceaccountservice.dto.match;

import com.beeswork.balanceaccountservice.constant.ClickResult;
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
public class MatchDTO {
    private ClickResult clickResult;
    private Long chatId;
    private UUID matcherId;
    private UUID matchedId;
    private Boolean active;
    private Boolean unmatched;
    private String name;
    private String repPhotoKey;
    private Boolean deleted;
    private Date createdAt;
    private Date updatedAt;

    public MatchDTO(ClickResult clickResult) {
        this.clickResult = clickResult;
    }

    public MatchDTO(ClickResult clickResult, UUID matcherId, UUID matchedId, String repPhotoKey) {
        this.matcherId = matcherId;
        this.clickResult = clickResult;
        this.matchedId = matchedId;
        this.repPhotoKey = repPhotoKey;
    }

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
