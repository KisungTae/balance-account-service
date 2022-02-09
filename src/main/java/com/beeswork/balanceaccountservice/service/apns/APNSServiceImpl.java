package com.beeswork.balanceaccountservice.service.apns;

import com.beeswork.balanceaccountservice.dto.common.Pushable;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class APNSServiceImpl implements APNSService {
    @Override
    public void push(Pushable pushable, Locale locale) {
        //todo: implement
    }
}
