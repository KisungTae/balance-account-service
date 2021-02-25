package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.constant.PushNotificationType;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.response.EmptyJsonResponse;
import com.beeswork.balanceaccountservice.service.account.AccountService;
import com.beeswork.balanceaccountservice.vm.account.SavePushNotificationTokenVM;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@RequestMapping("/push-notification")
public class PushNotificationController extends BaseController {

    private final AccountService accountService;

    @Autowired
    public PushNotificationController(ObjectMapper objectMapper, ModelMapper modelMapper, AccountService accountService) {
        super(objectMapper, modelMapper);
        this.accountService = accountService;
    }


    @PostMapping("/token/fcm")
    public ResponseEntity<String> saveFCMToken(@Valid @RequestBody SavePushNotificationTokenVM savePushNotificationTokenVM,
                                               BindingResult bindingResult)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        accountService.savePushNotificationToken(savePushNotificationTokenVM.getAccountId(),
                                                 savePushNotificationTokenVM.getIdentityToken(),
                                                 savePushNotificationTokenVM.getToken(),
                                                 PushNotificationType.FCM);
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @PostMapping("/token/aps")
    public ResponseEntity<String> saveAPSToken(@Valid @RequestBody SavePushNotificationTokenVM savePushNotificationTokenVM,
                                               BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        accountService.savePushNotificationToken(savePushNotificationTokenVM.getAccountId(),
                                                 savePushNotificationTokenVM.getIdentityToken(),
                                                 savePushNotificationTokenVM.getToken(),
                                                 PushNotificationType.APS);
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }
}
