package com.beeswork.balanceaccountservice.dto.firebase;

import com.beeswork.balanceaccountservice.constant.FCMDataKey;
import com.beeswork.balanceaccountservice.constant.NotificationType;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class FCMNotificationDTO {

    private String              token;
    private Map<String, String> messages = new HashMap<>();

    public static FCMNotificationDTO matchNotification(String token, String matchedPhotoKey) {
        return matchNotification(token, NotificationType.MATCH, matchedPhotoKey);
    }

    public static FCMNotificationDTO clickedNotification(String token, String clickedPhotoKey) {
        return matchNotification(token, NotificationType.CLICKED, clickedPhotoKey);
    }

    private static FCMNotificationDTO matchNotification(String token, String notificationType, String photoKey) {
        FCMNotificationDTO fcmNotificationDTO = new FCMNotificationDTO();
        fcmNotificationDTO.setToken(token);
        fcmNotificationDTO.getMessages().put(FCMDataKey.NOTIFICATION_TYPE, notificationType);
        fcmNotificationDTO.getMessages().put(FCMDataKey.PHOTO_KEY, photoKey);
        fcmNotificationDTO.getMessages().put(FCMDataKey.MESSAGE, "");
        return fcmNotificationDTO;
    }
}
