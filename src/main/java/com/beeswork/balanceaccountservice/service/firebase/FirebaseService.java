package com.beeswork.balanceaccountservice.service.firebase;

import com.beeswork.balanceaccountservice.dto.firebase.FCMNotificationDTO;
import com.beeswork.balanceaccountservice.dto.swipe.ClickDTO;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.messaging.FirebaseMessagingException;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface FirebaseService {

    void sendNotification(ClickDTO clickDTO, Locale locale);
}