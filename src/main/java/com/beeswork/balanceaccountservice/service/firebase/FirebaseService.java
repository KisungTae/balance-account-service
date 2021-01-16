package com.beeswork.balanceaccountservice.service.firebase;

import com.beeswork.balanceaccountservice.dto.firebase.AbstractFirebaseNotification;
import com.beeswork.balanceaccountservice.dto.firebase.FirebaseNotification;
import com.beeswork.balanceaccountservice.dto.swipe.ClickDTO;

import java.util.Locale;

public interface FirebaseService {

    void sendNotification(FirebaseNotification firebaseNotification, Locale locale);
    void sendNotification(ClickDTO clickDTO, Locale locale);
}