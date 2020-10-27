package com.beeswork.balanceaccountservice.dto.swipe;

import com.beeswork.balanceaccountservice.constant.NotificationType;
import com.beeswork.balanceaccountservice.dto.firebase.FCMNotificationDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ClickDTO {

    private boolean isClicked;
    private String notificationType;
    private UUID swipedId;
    private String swipedPhotoKey;

    @JsonIgnore
    private FCMNotificationDTO fcmNotificationDTO;

    public void setupAsClicked(UUID swipedId, String swipedPhotoKey, String swipedFCMToken) {
        this.swipedId = swipedId;
        this.swipedPhotoKey = swipedPhotoKey;
        this.notificationType = NotificationType.CLICKED;
        this.fcmNotificationDTO = FCMNotificationDTO.clickedNotification(swipedFCMToken, swipedPhotoKey);
    }

    public void setupAsMatch(UUID swipedId, String swipedPhotoKey, String swipedFCMToken) {
        this.swipedId = swipedId;
        this.swipedPhotoKey = swipedPhotoKey;
        this.notificationType = NotificationType.MATCH;
        this.fcmNotificationDTO = FCMNotificationDTO.matchNotification(swipedFCMToken, swipedPhotoKey);
    }
}
