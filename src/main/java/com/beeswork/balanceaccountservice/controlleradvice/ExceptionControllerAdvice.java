package com.beeswork.balanceaccountservice.controlleradvice;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.exception.BaseException;
import com.beeswork.balanceaccountservice.exception.account.*;
import com.beeswork.balanceaccountservice.exception.photo.PhotoNotFoundException;
import com.beeswork.balanceaccountservice.exception.question.QuestionNotFoundException;
import com.beeswork.balanceaccountservice.exception.question.QuestionSetChangedException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeClickedExistsException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeNotFoundException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipedBlockedException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipedNotFoundException;
import com.beeswork.balanceaccountservice.response.ExceptionResponse;
import com.beeswork.balanceaccountservice.vm.account.AccountEmailDuplicateException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@RestControllerAdvice(annotations = RestController.class)
public class ExceptionControllerAdvice {

    private static final String INTERNAL_SERVER_EXCEPTION = "internal.server.exception";
    private static final String QUERY_EXCEPTION = "query.exception";

    private final MessageSource messageSource;
    private final ObjectMapper objectMapper;

    @Autowired
    public ExceptionControllerAdvice(MessageSource messageSource, ObjectMapper objectMapper) {
        this.messageSource = messageSource;
        this.objectMapper = objectMapper;
    }

    //  TEST 1. if exception is thrown inside handleNotFoundException, then it will throw handleNotFoundException not route to General Exception handler
    @ExceptionHandler({AccountNotFoundException.class, QuestionNotFoundException.class, SwipeNotFoundException.class,
                       PhotoNotFoundException.class, AccountQuestionNotFoundException.class})
    public ResponseEntity<String> handleNotFoundException(BaseException exception, Locale locale)
    throws Exception {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(exceptionResponse(exception.getExceptionCode(), locale));
    }

    @ExceptionHandler({AccountShortOfPointException.class, AccountBlockedException.class,
                       SwipeClickedExistsException.class, BadRequestException.class,
                       SwipedBlockedException.class, SwipedNotFoundException.class,
                       QuestionSetChangedException.class, AccountEmailNotMutableException.class,
                       AccountEmailDuplicateException.class})
    public ResponseEntity<String> handleBadRequestException(BaseException exception, Locale locale)
    throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(exceptionResponse(exception.getExceptionCode(), locale));
    }

    @ExceptionHandler({AmazonServiceException.class, SdkClientException.class})
    public ResponseEntity<String> handleAWSException(Exception exception, Locale locale) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(messageSource.getMessage(QUERY_EXCEPTION, null, locale));
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
