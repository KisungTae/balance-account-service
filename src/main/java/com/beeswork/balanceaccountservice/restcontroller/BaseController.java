package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.util.Convert;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

public class BaseController {

    protected final Convert convert;

    public BaseController(Convert convert) {
        this.convert = convert;
    }

    public ResponseEntity<String> fieldErrorsResponse(BindingResult bindingResult) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(convert.fieldErrorsToJson(bindingResult));
    }
}
