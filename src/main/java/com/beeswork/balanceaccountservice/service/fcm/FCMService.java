package com.beeswork.balanceaccountservice.service.fcm;

import com.beeswork.balanceaccountservice.dto.firebase.FirebaseNotification;
import com.beeswork.balanceaccountservice.dto.swipe.ClickDTO;

import java.util.Locale;

public interface FCMService {

    void sendNotification(FirebaseNotification firebaseNotification, Locale locale);
    void sendNotification(ClickDTO clickDTO, Locale locale);
}