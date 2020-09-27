package com.beeswork.balanceaccountservice.controlleradvice;


import com.beeswork.balanceaccountservice.exception.BaseException;
import com.beeswork.balanceaccountservice.exception.account.AccountInvalidException;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.exception.account.AccountShortOfPointException;
import com.beeswork.balanceaccountservice.exception.match.MatchExistsException;
import com.beeswork.balanceaccountservice.exception.question.QuestionNotFoundException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeBalancedExistsException;
import com.beeswork.balanceaccountservice.util.Convert;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@RestControllerAdvice(annotations = RestController.class)
public class ExceptionControllerAdvice {

    private final Convert convert;

    @Autowired
    public ExceptionControllerAdvice(Convert convert) {
        this.convert = convert;
    }

//  TEST 1. if exception is thrown inside handleNotFoundException, then it will throw handleNotFoundException not route to General Exception handler
    @ExceptionHandler({AccountNotFoundException.class, QuestionNotFoundException.class})
    public ResponseEntity<String> handleNotFoundException(BaseException exception, Locale locale)
    throws Exception {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(convert.exceptionToJSON(exception.getExceptionCode(), locale));
    }

    @ExceptionHandler({AccountInvalidException.class, SwipeBalancedExistsException.class, AccountShortOfPointException.class,
                       MatchExistsException.class})
    public ResponseEntity<String> handleBadRequestException(BaseException exception, Locale locale)
    throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(convert.exceptionToJSON(exception.getExceptionCode(), locale));
    }

//    @ExceptionHandler({QueryException.class})
//    public ResponseEntity<String> handleQueryException(QueryException exception, Locale locale) {
//        exception.
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                             .contentType(MediaType.APPLICATION_JSON)
//                             .body(messageSource.getMessage(ExceptionCode.QUERY_EXCEPTION, null, locale));
//    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<String> handleException(Exception exception, Locale locale) {
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                             .contentType(MediaType.APPLICATION_JSON)
//                             .body(messageSource.getMessage(ExceptionCode.EXCEPTION, null, locale));
//    }
}
