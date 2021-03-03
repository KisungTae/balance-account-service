package com.beeswork.balanceaccountservice.dto.swipe;

import com.beeswork.balanceaccountservice.dto.push.ClickedNotification;
import com.beeswork.balanceaccountservice.dto.push.MatchedNotification;
import com.beeswork.balanceaccountservice.dto.push.Notification;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ClickDTO {

    public enum Result {
        MISS,
        CLICK,
        MATCH
    }

    private Result result = Result.MISS;
    private MatchDTO matchDTO;
    private Notification notification;

    public void setupAsClicked(UUID swiperId, String swiperRepPhotoKey, UUID clickedId) {
        this.result = Result.CLICK;
        this.matchDTO = new MatchDTO(clickedId);
        this.notification = new ClickedNotification(swiperId, swiperRepPhotoKey);
    }

    public void setupAsMatched(long chatId,
                               UUID matchedId,
                               String matchedName,
                               String matchedRepPhotoKey,
                               UUID matcherId,
                               String matcherName,
                               String matcherRepPhotoKey,
                               Date createdAt) {
        this.result = Result.MATCH;
        this.matchDTO = new MatchDTO(chatId, matchedId, matchedName, matchedRepPhotoKey, createdAt);
        this.notification = new MatchedNotification(chatId, matcherId, matcherName, matcherRepPhotoKey, createdAt);
    }
}
