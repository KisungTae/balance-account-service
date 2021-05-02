package com.beeswork.balanceaccountservice.service.push;

import com.beeswork.balanceaccountservice.service.apns.APNSService;
import com.beeswork.balanceaccountservice.service.fcm.FCMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PushServiceImpl implements PushService {

    private final FCMService fcmService;
    private final APNSService apnsService;

    @Autowired
    public PushServiceImpl(FCMService fcmService, APNSService apnsService) {
        this.fcmService = fcmService;
        this.apnsService = apnsService;
    }
}
