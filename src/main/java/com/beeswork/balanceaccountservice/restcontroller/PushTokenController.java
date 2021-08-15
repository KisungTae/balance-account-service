package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.constant.PushTokenType;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.response.EmptyJsonResponse;
import com.beeswork.balanceaccountservice.service.pushtoken.PushTokenService;
import com.beeswork.balanceaccountservice.vm.pushtoken.SavePushTokenVM;
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
@RequestMapping("/push-token")
public class PushTokenController extends BaseController {

    private final PushTokenService pushTokenService;

    @Autowired
    public PushTokenController(ObjectMapper objectMapper,
                               ModelMapper modelMapper,
                               PushTokenService pushTokenService) {
        super(objectMapper, modelMapper);
        this.pushTokenService = pushTokenService;
    }

    @PostMapping("/fcm")
    public ResponseEntity<String> saveFCMToken(@Valid @RequestBody SavePushTokenVM savePushTokenVM,
                                               BindingResult bindingResult)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        pushTokenService.savePushToken(savePushTokenVM.getAccountId(),
                                       savePushTokenVM.getIdentityToken(),
                                       savePushTokenVM.getToken(),
                                       PushTokenType.FCM);
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @PostMapping("/aps")
    public ResponseEntity<String> saveAPSToken(@Valid @RequestBody SavePushTokenVM savePushTokenVM,
                                               BindingResult bindingResult)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        pushTokenService.savePushToken(savePushTokenVM.getAccountId(),
                                       savePushTokenVM.getIdentityToken(),
                                       savePushTokenVM.getToken(),
                                       PushTokenType.APS);
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

}
