package com.beeswork.balanceaccountservice.service.fcm;

import com.beeswork.balanceaccountservice.dto.push.Push;

import java.util.Locale;

public interface FCMService {

    void sendPush(Push push, Locale locale);
}