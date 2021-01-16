package com.beeswork.balanceaccountservice.dto.swipe;

import com.beeswork.balanceaccountservice.projection.MatchProjection;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class ClickDTO {

    public enum Result {
        MISS,
        CLICK,
        MATCH
    }

    private Result          result = Result.MISS;
    private MatchProjection match;

    public void setupAsClick(UUID swipedId, Date swipeUpdatedAt) {
        this.result = Result.CLICK;
        this.match = new MatchProjection(swipedId, swipeUpdatedAt);
    }

    public void setupAsMatch(Long chatId, UUID swipedId, String swipedName, String matchedPhotoKey, Date matchUpdated) {
        this.result = Result.MATCH;
        this.match = new MatchProjection(chatId, swipedId, swipedName, matchedPhotoKey, matchUpdated);
    }
}
