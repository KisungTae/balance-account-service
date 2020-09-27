package com.beeswork.balanceaccountservice.util;

import com.beeswork.balanceaccountservice.exception.BaseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component
public class Convert {

    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;

    @Autowired
    public Convert(ObjectMapper objectMapper, MessageSource messageSource) {
        this.objectMapper = objectMapper;
        this.messageSource = messageSource;
    }

    public String exceptionToJSON(String exceptionCode, Locale locale) throws JsonProcessingException {

        String exceptionMessage = messageSource.getMessage(exceptionCode, null, locale);
        Map<String, String> map = new HashMap<>();
        map.put("exceptionCode", exceptionCode);
        map.put("exceptionMessage", exceptionMessage);
        return objectMapper.writeValueAsString(map);
    }

}
