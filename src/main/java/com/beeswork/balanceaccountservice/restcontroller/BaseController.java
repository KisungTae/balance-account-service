package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.constant.ExceptionCode;
import com.beeswork.balanceaccountservice.response.ExceptionResponse;
import com.beeswork.balanceaccountservice.util.Convert;
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

    public ResponseEntity<String> fieldExceptionResponse(BindingResult bindingResult) throws JsonProcessingException {

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors())
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());

        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.value(),
                                                                    ExceptionCode.FIELD_EXCEPTION,
                                                                    "",
                                                                    fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectMapper.writeValueAsString(exceptionResponse));
    }


}
