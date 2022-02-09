package com.beeswork.balanceaccountservice.service.apns;

import com.beeswork.balanceaccountservice.dto.common.Pushable;

import java.util.Locale;

public interface APNSService {
    void push(Pushable pushable, Locale locale);
}
