package com.beeswork.balanceaccountservice.dto.firebase;

import com.beeswork.balanceaccountservice.constant.NotificationType;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class FCMNotificationDTO {

    private static final String NOTIFICATION_TYPE = "notificationType";
    private static final String PHOTO_KEY = "photoKey";
    private static final String MESSAGE = "message";
    private static final String NAME = "name";
    private static final String MATCHED_ID = "matchedId";
    private static final String SWIPED_ID = "swipedId";
    private static final String CHAT_ID = "chatId";
    private static final String UPDATED_AT = "updatedAt";

    private String              token;
    private Map<String, String> data = new HashMap<>();

    public static FCMNotificationDTO matchNotification(String token, String matchedId, String name, String chatId,
                                                       String matchedPhotoKey) {

        FCMNotificationDTO fcmNotificationDTO = notification(token, NotificationType.MATCH, matchedPhotoKey);
        fcmNotificationDTO.getData().put(MATCHED_ID, matchedId);
        fcmNotificationDTO.getData().put(NAME, name);
        fcmNotificationDTO.getData().put(CHAT_ID, chatId);
        return fcmNotificationDTO;
    }


    public static FCMNotificationDTO clickedNotification(String token, String swipedId, String clickedPhotoKey, String updatedAt) {

        FCMNotificationDTO fcmNotificationDTO = notification(token, NotificationType.CLICKED, clickedPhotoKey);
        fcmNotificationDTO.getData().put(SWIPED_ID, swipedId);
        fcmNotificationDTO.getData().put(UPDATED_AT, updatedAt);
        return fcmNotificationDTO;
    }

    private static FCMNotificationDTO notification(String token, String notificationType, String photoKey) {

        FCMNotificationDTO fcmNotificationDTO = new FCMNotificationDTO();
        fcmNotificationDTO.setToken(token);
        fcmNotificationDTO.getData().put(NOTIFICATION_TYPE, notificationType);
        fcmNotificationDTO.getData().put(PHOTO_KEY, photoKey);
        return fcmNotificationDTO;
    }
}
