package com.beeswork.balanceaccountservice.controlleradvice;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.beeswork.balanceaccountservice.constant.PhotoConstant;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.exception.BaseException;
import com.beeswork.balanceaccountservice.exception.account.*;
import com.beeswork.balanceaccountservice.exception.login.EmailNotMutableException;
import com.beeswork.balanceaccountservice.exception.login.InvalidSocialLoginException;
import com.beeswork.balanceaccountservice.exception.login.LoginNotFoundException;
import com.beeswork.balanceaccountservice.exception.match.MatchNotFoundException;
import com.beeswork.balanceaccountservice.exception.photo.PhotoAlreadyExistsException;
import com.beeswork.balanceaccountservice.exception.photo.PhotoExceededMaxException;
import com.beeswork.balanceaccountservice.exception.photo.PhotoInvalidDeleteException;
import com.beeswork.balanceaccountservice.exception.photo.PhotoNotFoundException;
import com.beeswork.balanceaccountservice.exception.profile.ProfileNotFoundException;
import com.beeswork.balanceaccountservice.exception.question.QuestionNotFoundException;
import com.beeswork.balanceaccountservice.exception.question.QuestionSetChangedException;
import com.beeswork.balanceaccountservice.exception.report.ReportReasonNotFoundException;
import com.beeswork.balanceaccountservice.exception.report.ReportedNotFoundException;
import com.beeswork.balanceaccountservice.exception.setting.SettingNotFoundException;
import com.beeswork.balanceaccountservice.exception.stomp.QueueNotFoundException;
import com.beeswork.balanceaccountservice.exception.swipe.*;
import com.beeswork.balanceaccountservice.response.ExceptionResponse;
import com.beeswork.balanceaccountservice.exception.login.EmailDuplicateException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@RestControllerAdvice(annotations = RestController.class)
public class ExceptionControllerAdvice {

    private static final String INTERNAL_SERVER_EXCEPTION = "internal.server.exception";
    private static final String QUERY_EXCEPTION           = "query.exception";
    private static final String PERSISTENCE_EXCEPTION     = "persistence.exception";

    private final MessageSource messageSource;
    private final ObjectMapper  objectMapper;

    @Autowired
    public ExceptionControllerAdvice(MessageSource messageSource, ObjectMapper objectMapper) {
        this.messageSource = messageSource;
        this.objectMapper = objectMapper;
    }

    //  TEST 1. if exception is thrown inside handleNotFoundException, then it will throw handleNotFoundException not route to General Exception handler
    @ExceptionHandler({AccountNotFoundException.class, QuestionNotFoundException.class,
                       SwipeNotFoundException.class, PhotoNotFoundException.class,
                       AccountQuestionNotFoundException.class, LoginNotFoundException.class,
                       ProfileNotFoundException.class, MatchNotFoundException.class,
                       ReportReasonNotFoundException.class, ReportedNotFoundException.class,
                       QueueNotFoundException.class, SwipeMetaNotFoundException.class,
                       SettingNotFoundException.class})
    public ResponseEntity<String> handleNotFoundException(BaseException exception, Locale locale)
    throws Exception {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(exceptionResponse(exception.getExceptionCode(), null, locale));
    }

    @ExceptionHandler({UsernameNotFoundException.class})
    public ResponseEntity<String> handleUserNameNotFoundException(UsernameNotFoundException exception, Locale locale)
    throws Exception {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(exceptionResponse("user.name.not.found.exception", null, locale));
    }

    @ExceptionHandler({AccountShortOfPointException.class, AccountBlockedException.class,
                       SwipeClickedExistsException.class, BadRequestException.class,
                       SwipedBlockedException.class, SwipedNotFoundException.class,
                       QuestionSetChangedException.class, EmailNotMutableException.class,
                       EmailDuplicateException.class, PhotoInvalidDeleteException.class,
                       AccountDeletedException.class, PhotoAlreadyExistsException.class,
                       PhotoExceededMaxException.class, InvalidSocialLoginException.class})
    public ResponseEntity<String> handleBadRequestException(BaseException exception, Locale locale)
    throws JsonProcessingException {
        Object[] arguments = null;
        if (exception instanceof PhotoExceededMaxException)
            arguments = new Object[] {PhotoConstant.MAX_NUM_OF_PHOTOS};

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(exceptionResponse(exception.getExceptionCode(), arguments, locale));
    }

    @MessageExceptionHandler(Exception.class)
    public ResponseEntity<String> handleMessageException(Exception e) {
//        TODO: log exception for later review
        System.out.println("handleMessageException");
        System.out.println(e.getLocalizedMessage());
//        System.out.println(e.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("abc error ");
    }

    @ExceptionHandler({AmazonServiceException.class, SdkClientException.class})
    public ResponseEntity<String> handleAWSException(Exception exception, Locale locale) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(messageSource.getMessage(QUERY_EXCEPTION, null, locale));
    }

//    @ExceptionHandler({PersistenceException.class})
//    public ResponseEntity<String> handlePersistenceException(PersistenceException exception, Locale locale) {
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                             .contentType(MediaType.APPLICATION_JSON)
//                             .body(messageSource.getMessage(PERSISTENCE_EXCEPTION, null, locale));
//    }
//
//    @ExceptionHandler({QueryException.class})
//    public ResponseEntity<String> handleQueryException(QueryException exception, Locale locale) {
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                             .contentType(MediaType.APPLICATION_JSON)
//                             .body(messageSource.getMessage(QUERY_EXCEPTION, null, locale));
//    }
//
//    @ExceptionHandler({Exception.class})
//    public ResponseEntity<String> handleException(Locale locale) throws JsonProcessingException {
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                             .contentType(MediaType.APPLICATION_JSON)
//                             .body(exceptionResponse(INTERNAL_SERVER_EXCEPTION, locale));
//    }

    private String exceptionResponse(String exceptionCode, Object[] object, Locale locale) throws JsonProcessingException {
        String exceptionMessage = messageSource.getMessage(exceptionCode, object, locale);
        ExceptionResponse exceptionResponse = new ExceptionResponse(exceptionCode, exceptionMessage, null);
        return objectMapper.writeValueAsString(exceptionResponse);
    }
}
