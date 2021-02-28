package com.beeswork.balanceaccountservice.restcontroller;


import com.beeswork.balanceaccountservice.constant.PushTokenType;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.response.EmptyJsonResponse;
import com.beeswork.balanceaccountservice.service.account.AccountService;
import com.beeswork.balanceaccountservice.vm.account.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/account")
public class AccountController extends BaseController {

    private final AccountService accountService;

    @Autowired
    public AccountController(ObjectMapper objectMapper, ModelMapper modelMapper, AccountService accountService) {
        super(objectMapper, modelMapper);
        this.accountService = accountService;
    }

    @PostMapping("/token/fcm")
    public ResponseEntity<String> saveFCMToken(@Valid @RequestBody SavePushTokenVM savePushTokenVM,
                                               BindingResult bindingResult)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        accountService.savePushToken(savePushTokenVM.getAccountId(),
                                     savePushTokenVM.getIdentityToken(),
                                     savePushTokenVM.getKey(),
                                     PushTokenType.FCM);
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @PostMapping("/token/aps")
    public ResponseEntity<String> saveAPSToken(@Valid @RequestBody SavePushTokenVM savePushTokenVM,
                                               BindingResult bindingResult)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        accountService.savePushToken(savePushTokenVM.getAccountId(),
                                     savePushTokenVM.getIdentityToken(),
                                     savePushTokenVM.getKey(),
                                     PushTokenType.APS);
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteAccount(@Valid @RequestBody AccountIdentityVM accountIdentityVM,
                                                BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        accountService.deleteAccount(accountIdentityVM.getAccountId(), accountIdentityVM.getIdentityToken());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @PostMapping("/answers")
    public ResponseEntity<String> saveAnswers(@Valid @RequestBody SaveAnswersVM saveAnswersVM,
                                              BindingResult bindingResult)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        accountService.saveAnswers(saveAnswersVM.getAccountId(),
                                   saveAnswersVM.getIdentityToken(),
                                   saveAnswersVM.getAnswers());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @PostMapping("/email")
    public ResponseEntity<String> saveEmail(@Valid @RequestBody SaveEmailVM saveEmailVM,
                                            BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) super.fieldExceptionResponse(bindingResult);
        accountService.saveEmail(saveEmailVM.getAccountId(), saveEmailVM.getIdentityToken(), saveEmailVM.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }
}
