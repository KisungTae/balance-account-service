package com.beeswork.balanceaccountservice.controlleradvice;


import com.beeswork.balanceaccountservice.constant.ExceptionCode;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.exception.BaseException;
import com.beeswork.balanceaccountservice.exception.account.*;
import com.beeswork.balanceaccountservice.exception.question.QuestionNotFoundException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeClickedExistsException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeNotFoundException;
import com.beeswork.balanceaccountservice.response.ExceptionResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.Http;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice(annotations = RestController.class)
public class ExceptionControllerAdvice {

    private final MessageSource messageSource;
    private final ObjectMapper objectMapper;

    @Autowired
    public ExceptionControllerAdvice(MessageSource messageSource, ObjectMapper objectMapper) {
        this.messageSource = messageSource;
        this.objectMapper = objectMapper;
    }

    //  TEST 1. if exception is thrown inside handleNotFoundException, then it will throw handleNotFoundException not route to General Exception handler
    @ExceptionHandler({AccountNotFoundException.class, QuestionNotFoundException.class, SwipeNotFoundException.class})
    public ResponseEntity<String> handleNotFoundException(BaseException exception, Locale locale)
    throws Exception {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(exceptionResponse(exception.getExceptionCode(), locale));
    }

    @ExceptionHandler({AccountShortOfPointException.class, AccountBlockedException.class,
                       SwipeClickedExistsException.class, BadRequestException.class})
    public ResponseEntity<String> handleBadRequestException(BaseException exception, Locale locale)
    throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(exceptionResponse(exception.getExceptionCode(), locale));
    }


//    @ExceptionHandler({QueryException.class})
//    public ResponseEntity<String> handleQueryException(QueryException exception, Locale locale) {
//        exception.
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                             .contentType(MediaType.APPLICATION_JSON)
//                             .body(messageSource.getMessage(ExceptionCode.QUERY_EXCEPTION, null, locale));
//    }

//    @ExceptionHandler({Exception.class})
//    public ResponseEntity<String> handleException(Locale locale) throws JsonProcessingException {
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                             .contentType(MediaType.APPLICATION_JSON)
//                             .body(exceptionResponse(ExceptionCode.INTERNAL_SERVER_EXCEPTION, locale));
//    }

    private String exceptionResponse(String exceptionCode, Locale locale) throws JsonProcessingException {
        String exceptionMessage = messageSource.getMessage(exceptionCode, null, locale);
        ExceptionResponse exceptionResponse = new ExceptionResponse(exceptionCode, exceptionMessage, null);
        return objectMapper.writeValueAsString(exceptionResponse);
    }
}
