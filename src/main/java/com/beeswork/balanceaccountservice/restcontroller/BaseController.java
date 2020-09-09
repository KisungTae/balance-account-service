package com.beeswork.balanceaccountservice.restcontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

public class BaseController {

    protected final ObjectMapper objectMapper;
    protected final ModelMapper modelMapper;

    public BaseController(ObjectMapper objectMapper, ModelMapper modelMapper) {
        this.objectMapper = objectMapper;
        this.modelMapper = modelMapper;
    }

    public ResponseEntity<String> fieldErrorsResponse(BindingResult bindingResult) throws JsonProcessingException {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors())
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectMapper.writeValueAsString(errors));
    }


}
