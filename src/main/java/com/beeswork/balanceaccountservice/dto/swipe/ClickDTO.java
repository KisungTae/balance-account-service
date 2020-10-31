package com.beeswork.balanceaccountservice.dto.swipe;

import com.beeswork.balanceaccountservice.constant.NotificationType;
import com.beeswork.balanceaccountservice.dto.firebase.FCMNotificationDTO;
import com.beeswork.balanceaccountservice.projection.ClickedProjection;
import com.beeswork.balanceaccountservice.projection.MatchProjection;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class ClickDTO {

    private String          notificationType = NotificationType.NOT_CLICK;
    private MatchProjection match;

    @JsonIgnore
    private FCMNotificationDTO fcmNotificationDTO;

    public void setupAsClick(UUID swipedId, String swipedPhotoKey, String swipedFCMToken, Date updatedAt) {
        this.notificationType = NotificationType.CLICK;
        this.match = new MatchProjection();
        this.match.setMatchedId(swipedId);
        this.match.setPhotoKey(swipedPhotoKey);
        String isoUpdatedAt = DateTimeFormatter.ISO_INSTANT.format(updatedAt.toInstant());
        this.fcmNotificationDTO = FCMNotificationDTO.clickedNotification(swipedFCMToken, swipedId.toString(),
                                                                         swipedPhotoKey,
                                                                         isoUpdatedAt);
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
        this.fcmNotificationDTO = FCMNotificationDTO.matchNotification(swipedFCMToken, swipedId.toString(), name,
                                                                       chatId, swipedPhotoKey);
    }
}
