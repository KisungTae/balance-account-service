package com.beeswork.balanceaccountservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

@Component

public final class Convert {

    private final ObjectMapper objectMapper;

    @Autowired
    public Convert(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String fieldErrorsToJson(BindingResult bindingResult) throws JsonProcessingException {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors())
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        return objectMapper.writeValueAsString(errors);
    }
}
