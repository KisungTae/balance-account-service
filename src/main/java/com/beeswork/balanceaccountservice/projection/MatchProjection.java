package com.beeswork.balanceaccountservice.projection;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class MatchProjection {

    private UUID matchedId;
    private String name;
    private String photoKey;
    private boolean unmatched;

    @QueryProjection
    public MatchProjection(UUID matchedId, String name, String photoKey, boolean unmatched) {
        this.matchedId = matchedId;
        this.name = name;
        this.photoKey = photoKey;
        this.unmatched = unmatched;
    }
}
