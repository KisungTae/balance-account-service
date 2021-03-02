package com.beeswork.balanceaccountservice.dto.swipe;

import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
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

    private Result   result = Result.MISS;
    private MatchDTO matchDTO;

    public void setupAsClick(UUID swipedId, Date updatedAt) {
        this.result = Result.CLICK;
        this.matchDTO = new MatchDTO(swipedId, updatedAt);
    }

    public void setupAsMatch(Long chatId, UUID matchedId, String name, String repPhotoKey, Date updatedAt) {
        this.result = Result.MATCH;
        this.matchDTO = new MatchDTO(chatId, matchedId, name, repPhotoKey, updatedAt);
    }
}
