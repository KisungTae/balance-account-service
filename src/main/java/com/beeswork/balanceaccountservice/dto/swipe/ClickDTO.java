package com.beeswork.balanceaccountservice.dto.swipe;

import com.beeswork.balanceaccountservice.constant.NotificationType;
import com.beeswork.balanceaccountservice.projection.MatchProjection;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class ClickDTO {

    enum ClickResult {
        NOT_CLICK,
        CLICK,
        MATCH
    }

    private ClickResult clickResult = ClickResult.NOT_CLICK;
    private String swipedId;
    private String swipedPhotoKey;
    private String swipedFCMToken;
    private String swipedName;
    private Long chatId;
    private Date updatedAt;

    public void setupAsClick(UUID swipedId, String swipedPhotoKey, String swipedFCMToken, Date updatedAt) {
        this.clickResult = ClickResult.CLICK;

        this.match = new MatchProjection();
        this.match.setMatchedId(swipedId);
        this.match.setPhotoKey(swipedPhotoKey);
        this.match.setUpdatedAt(updatedAt);
        this.fcmToken = swipedFCMToken;
    }

    public void setupAsMatch(UUID swipedId, String name, String swipedPhotoKey, Long chatId, String swipedFCMToken,
                             boolean unmatched) {

        this.notificationType = NotificationType.MATCH;
        this.match = new MatchProjection();
        this.match.setMatchedId(swipedId);
        this.match.setName(name);
        this.match.setPhotoKey(swipedPhotoKey);
        this.match.setChatId(chatId);
        this.match.setUnmatched(unmatched);
        this.fcmToken = swipedFCMToken;
    }
}
