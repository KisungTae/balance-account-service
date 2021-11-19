package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.response.ExceptionResponse;
import com.beeswork.balanceaccountservice.util.Convert;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BaseController {

    private static final String FIELD_EXCEPTION = "field.exception";

    protected final int MAX_OPTIMISTIC_LOCK_EXCEPTION_RETRY_COUNT = 5;

    protected final ObjectMapper objectMapper;
    protected final ModelMapper modelMapper;

    public BaseController(ObjectMapper objectMapper, ModelMapper modelMapper) {
        this.objectMapper = objectMapper;
        this.modelMapper = modelMapper;
    }

    protected ResponseEntity<String> fieldExceptionResponse(BindingResult bindingResult) throws JsonProcessingException {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        ExceptionResponse exceptionResponse = new ExceptionResponse(FIELD_EXCEPTION, "", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectMapper.writeValueAsString(exceptionResponse));
    }

    protected UUID getAccountIdFrom(Principal principal) {
        if (principal == null) throw new AccountNotFoundException();
        UUID accountId = Convert.toUUID(principal.getName());
        if (accountId == null) throw new AccountNotFoundException();
        return accountId;
    }


}
