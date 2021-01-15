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
        NOT_CLICKED,
        CLICKED,
        MATCHED,
        MESSAGE
    }

    private Result result = Result.NOT_CLICKED;

    private String swiperName;
    private String swiperPhotoKey;

    private UUID swipedId;
    private String swipedFCMToken;
    private Date swipeUpdatedAt;

    private MatchProjection match;

    public void setupAsClick(String swiperName, String swiperPhotoKey, UUID swipedId, String swipedFCMToken, Date swipeUpdatedAt) {

        this.result = Result.CLICKED;
        this.swiperName = swiperName;
        this.swiperPhotoKey = swiperPhotoKey;
        this.match = new MatchProjection(swipedId, swipeUpdatedAt);
        this.swipedFCMToken = swipedFCMToken;

    }

    public void setupAsMatch(String swiperName, String swiperPhotoKey, Long chatId, UUID swipedId, String swipedName,
                             String swipedPhotoKey, String swipedFCMToken, Date matchUpdated) {

        this.result = Result.MATCHED;
        this.swiperName = swiperName;
        this.swiperPhotoKey = swiperPhotoKey;
        this.match = new MatchProjection(chatId, swipedId, swipedName, swipedPhotoKey, matchUpdated);
        this.swipedFCMToken = swipedFCMToken;
    }
}
