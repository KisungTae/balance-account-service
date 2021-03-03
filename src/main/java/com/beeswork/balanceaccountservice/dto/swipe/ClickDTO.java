package com.beeswork.balanceaccountservice.dto.swipe;

import com.beeswork.balanceaccountservice.dto.push.ClickedPush;
import com.beeswork.balanceaccountservice.dto.push.MatchedPush;
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

    private Push clickPush;
    private Push clickedPush;

    public void setupAsClicked(UUID clickerId, String clickerRepPhotoKey, UUID clickedId) {
        this.clickPush = new ClickedPush(clickedId, null);
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
        this.clickPush = new MatchedPush(chatId, matchedId, matchedName, matchedRepPhotoKey, createdAt);
        this.clickedPush = new MatchedPush(chatId, matcherId, matcherName, matcherRepPhotoKey, createdAt);
    }
}
