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

    private Long chatId;
    private UUID matchedId;
    private String name;
    private String photoKey;
    private boolean unmatched;
    private Date updatedAt;

    @QueryProjection
    public MatchProjection(Long chatId, UUID matchedId, String name, String photoKey, boolean unmatched, Date updatedAt) {
        this.chatId = chatId;
        this.matchedId = matchedId;
        this.name = name;
        this.photoKey = photoKey;
        this.unmatched = unmatched;
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "MatchProjection{" +
               "matchedId=" + matchedId +
               ", name='" + name + '\'' +
               ", photoKey='" + photoKey + '\'' +
               ", unmatched=" + unmatched +
               ", updatedAt=" + updatedAt +
               '}';
    }
}
