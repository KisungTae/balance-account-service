package com.beeswork.balanceaccountservice.controlleradvice;


import com.beeswork.balanceaccountservice.constant.PhotoConstant;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.exception.BaseException;
import com.beeswork.balanceaccountservice.exception.InternalServerException;
import com.beeswork.balanceaccountservice.exception.account.*;
import com.beeswork.balanceaccountservice.exception.chat.ChatMessageNotFoundException;
import com.beeswork.balanceaccountservice.exception.jwt.ExpiredJWTException;
import com.beeswork.balanceaccountservice.exception.jwt.InvalidJWTTokenException;
import com.beeswork.balanceaccountservice.exception.jwt.InvalidRefreshTokenException;
import com.beeswork.balanceaccountservice.exception.login.*;
import com.beeswork.balanceaccountservice.exception.match.MatchNotFoundException;
import com.beeswork.balanceaccountservice.exception.photo.PhotoAlreadyExistsException;
import com.beeswork.balanceaccountservice.exception.photo.PhotoExceededMaxException;
import com.beeswork.balanceaccountservice.exception.photo.PhotoInvalidDeleteException;
import com.beeswork.balanceaccountservice.exception.photo.PhotoNotFoundException;
import com.beeswork.balanceaccountservice.exception.profile.EmailDuplicateException;
import com.beeswork.balanceaccountservice.exception.profile.EmailNotMutableException;
import com.beeswork.balanceaccountservice.exception.profile.ProfileNotFoundException;
import com.beeswork.balanceaccountservice.exception.question.QuestionNotFoundException;
import com.beeswork.balanceaccountservice.exception.question.QuestionSetChangedException;
import com.beeswork.balanceaccountservice.exception.report.ReportReasonNotFoundException;
import com.beeswork.balanceaccountservice.exception.report.ReportedNotFoundException;
import com.beeswork.balanceaccountservice.exception.setting.SettingNotFoundException;
import com.beeswork.balanceaccountservice.exception.stomp.QueueNotFoundException;
import com.beeswork.balanceaccountservice.exception.swipe.*;
import com.beeswork.balanceaccountservice.exception.wallet.WalletNotFoundException;
import com.beeswork.balanceaccountservice.response.ExceptionResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.GeneralSecurityException;
import java.util.Locale;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
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
                       SettingNotFoundException.class, ChatMessageNotFoundException.class,
                       WalletNotFoundException.class})
    public ResponseEntity<String> handleNotFoundException(BaseException exception, Locale locale) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(exceptionResponse(exception.getExceptionCode(), null, locale));
    }

    @ExceptionHandler({UsernameNotFoundException.class})
    public ResponseEntity<String> handleUserNameNotFoundException(UsernameNotFoundException exception, Locale locale)
    throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(exceptionResponse("user.name.not.found.exception", null, locale));
    }

    @ExceptionHandler({GeneralSecurityException.class})
    public ResponseEntity<String> handleGeneralSecurityException(GeneralSecurityException exception, Locale locale)
    throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(exceptionResponse("general.security.exception", null, locale));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException exception, Locale locale)
    throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(exceptionResponse("authentication.exception", null, locale));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAuthenticationException(AccessDeniedException exception, Locale locale)
    throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(exceptionResponse("access.denied.exception", null, locale));
    }

    @ExceptionHandler(ExpiredJWTException.class)
    public ResponseEntity<String> handleExpiredJWTException(ExpiredJWTException exception, Locale locale)
    throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(exceptionResponse(exception.getExceptionCode(), null, locale));
    }

//    @ExceptionHandler(ExpiredJwtException.class)
//    public ResponseEntity<String> handleExpiredJwtException(ExpiredJwtException exception, Locale locale)
//    throws JsonProcessingException {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                             .contentType(MediaType.APPLICATION_JSON)
//                             .body(exceptionResponse("expired.jwt.exception", null, locale));
//    }

    @ExceptionHandler({InsufficientPointException.class, AccountBlockedException.class,
                       SwipeClickedExistsException.class, BadRequestException.class,
                       SwipedBlockedException.class, SwipedNotFoundException.class,
                       QuestionSetChangedException.class, EmailNotMutableException.class,
                       EmailDuplicateException.class, PhotoInvalidDeleteException.class,
                       AccountDeletedException.class, PhotoAlreadyExistsException.class,
                       InvalidSocialLoginException.class, InvalidRefreshTokenException.class,
                       InvalidJWTTokenException.class, SwipeMatchedExistsException.class})
    public ResponseEntity<String> handleBadRequestException(BaseException exception, Locale locale)
    throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(exceptionResponse(exception.getExceptionCode(), null, locale));
    }

    @ExceptionHandler({PhotoExceededMaxException.class})
    public ResponseEntity<String> handlePhotoExceededMaxException(PhotoExceededMaxException exception, Locale locale)
    throws JsonProcessingException {
        Object[] arguments = new Object[]{PhotoConstant.MAX_NUM_OF_PHOTOS};
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(exceptionResponse(exception.getExceptionCode(), arguments, locale));
    }

//    @MessageExceptionHandler(Exception.class)
//    public ResponseEntity<String> handleMessageException(Exception e) {
//        TODO: log exception for later review
//        System.out.println("handleMessageException");
//        System.out.println(e.getLocalizedMessage());
//        System.out.println(e.getLocalizedMessage());
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("abc error ");
//    }

//    @ExceptionHandler({AmazonServiceException.class, SdkClientException.class})
//    public ResponseEntity<String> handleAWSException(Exception exception, Locale locale) {
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                             .contentType(MediaType.APPLICATION_JSON)
//                             .body(messageSource.getMessage(QUERY_EXCEPTION, null, locale));
//    }

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

//    InvalidFormatException

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<String> handleInvalidFormatException(HttpMessageNotReadableException exception, Locale locale)
    throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(exceptionResponse(BadRequestException.CODE, null, locale));
    }

    @ExceptionHandler({InternalServerException.class})
    public ResponseEntity<String> handleInternalServerException(InternalServerException exception, Locale locale)
    throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(exceptionResponse(exception.getExceptionCode(), null, locale));
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<String> handleException(Exception exception, Locale locale) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(exceptionResponse(INTERNAL_SERVER_EXCEPTION, null, locale));
    }


    private String exceptionResponse(String exceptionCode, Object[] object, Locale locale) throws JsonProcessingException {
        String exceptionMessage = messageSource.getMessage(exceptionCode, object, locale);
        ExceptionResponse exceptionResponse = new ExceptionResponse(exceptionCode, exceptionMessage, null);
        return objectMapper.writeValueAsString(exceptionResponse);
    }
}
