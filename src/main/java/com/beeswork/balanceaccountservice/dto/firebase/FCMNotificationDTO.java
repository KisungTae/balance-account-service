package com.beeswork.balanceaccountservice.dto.firebase;

import com.beeswork.balanceaccountservice.constant.FCMDataKey;
import com.beeswork.balanceaccountservice.constant.NotificationType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class FCMNotificationDTO {

    private String              token;
    private Map<String, String> messages = new HashMap<>();



    public static FCMNotificationDTO matchNotification(String token, String matchedId, String name, String chatId,
                                                       String matchedPhotoKey, String message) {

        FCMNotificationDTO fcmNotificationDTO = notification(token, NotificationType.MATCH, matchedPhotoKey, message);
        fcmNotificationDTO.getMessages().put(FCMDataKey.MATCHED_ID, matchedId);
        fcmNotificationDTO.getMessages().put(FCMDataKey.NAME, name);
        fcmNotificationDTO.getMessages().put(FCMDataKey.CHAT_ID, chatId);
        return fcmNotificationDTO;
    }

    public static FCMNotificationDTO clickedNotification(String token, String swipedId, String clickedPhotoKey, String updatedAt, String message) {

        FCMNotificationDTO fcmNotificationDTO = notification(token, NotificationType.CLICKED, clickedPhotoKey,message);
        fcmNotificationDTO.getMessages().put(FCMDataKey.SWIPED_ID, swipedId);
        fcmNotificationDTO.getMessages().put(FCMDataKey.UPDATED_AT, updatedAt);
        return fcmNotificationDTO;
    }

    private static FCMNotificationDTO notification(String token, String notificationType, String photoKey,
                                                   String message) {

        FCMNotificationDTO fcmNotificationDTO = new FCMNotificationDTO();
        fcmNotificationDTO.setToken(token);
        fcmNotificationDTO.getMessages().put(FCMDataKey.NOTIFICATION_TYPE, notificationType);
        fcmNotificationDTO.getMessages().put(FCMDataKey.PHOTO_KEY, photoKey);
        fcmNotificationDTO.getMessages().put(FCMDataKey.MESSAGE, message);
        return fcmNotificationDTO;
    }
}
