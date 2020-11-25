package com.beeswork.balanceaccountservice.service.firebase;

import com.beeswork.balanceaccountservice.dto.swipe.ClickDTO;

import java.util.Locale;

public interface FirebaseService {

    void sendNotification(ClickDTO clickDTO, Locale locale);
}