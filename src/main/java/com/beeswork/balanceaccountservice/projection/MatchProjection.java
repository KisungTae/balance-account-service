package com.beeswork.balanceaccountservice.projection;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class MatchProjection {

    private UUID matchedId;
    private String photoKey;

    @QueryProjection
    public MatchProjection(UUID matchedId, String photoKey) {
        this.matchedId = matchedId;
        this.photoKey = photoKey;
    }
}
