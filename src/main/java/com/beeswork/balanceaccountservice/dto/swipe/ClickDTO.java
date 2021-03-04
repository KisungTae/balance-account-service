package com.beeswork.balanceaccountservice.dto.swipe;

import com.beeswork.balanceaccountservice.dto.push.ClickedPush;
import com.beeswork.balanceaccountservice.dto.push.MatchedPush;
import com.beeswork.balanceaccountservice.dto.push.MissedPush;
import com.beeswork.balanceaccountservice.dto.push.Push;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ClickDTO {

    private Push clickerPush;
    private Push clickedPush;

    public void setupAsMissed() {
        clickerPush = new MissedPush();
    }

    public void setupAsClicked(UUID clickerId, String clickerRepPhotoKey, UUID clickedId) {
        this.clickerPush = new ClickedPush(clickedId, null);
        this.clickedPush = new ClickedPush(clickerId, clickerRepPhotoKey);
    }

    public void setupAsMatched(long chatId,
                               UUID matchedId,
                               String matchedName,
                               String matchedRepPhotoKey,
                               UUID matcherId,
                               String matcherName,
                               String matcherRepPhotoKey,
                               Date createdAt) {
        this.clickerPush = new MatchedPush(chatId, matchedId, matchedName, matchedRepPhotoKey, createdAt);
        this.clickedPush = new MatchedPush(chatId, matcherId, matcherName, matcherRepPhotoKey, createdAt);
    }
}
