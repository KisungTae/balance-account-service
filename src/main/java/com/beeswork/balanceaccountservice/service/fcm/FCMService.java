package com.beeswork.balanceaccountservice.service.fcm;

import com.beeswork.balanceaccountservice.dto.push.Notification;
import com.beeswork.balanceaccountservice.dto.swipe.ClickDTO;

import java.util.Locale;

public interface FCMService {

    void sendNotification(Notification notification, Locale locale);
    void sendNotification(ClickDTO clickDTO, Locale locale);
}