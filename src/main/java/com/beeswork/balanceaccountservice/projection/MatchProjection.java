package com.beeswork.balanceaccountservice.projection;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class MatchProjection {

    private UUID matchedId;
    private String name;
    private String photoKey;
    private boolean unmatched;
    private Date updatedAt;

    @QueryProjection
    public MatchProjection(UUID matchedId, String name, String photoKey, boolean unmatched, Date updatedAt) {
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
