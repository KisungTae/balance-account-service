package com.beeswork.balanceaccountservice.restcontroller;


import com.beeswork.balanceaccountservice.dto.click.ClickDTO;
import com.beeswork.balanceaccountservice.dto.firebase.FCMNotificationDTO;
import com.beeswork.balanceaccountservice.exception.account.AccountInvalidException;
import com.beeswork.balanceaccountservice.exception.match.MatchExistsException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeNotFoundException;
import com.beeswork.balanceaccountservice.response.EmptyJsonResponse;
import com.beeswork.balanceaccountservice.service.click.ClickService;
import com.beeswork.balanceaccountservice.service.firebase.FCMService;
import com.beeswork.balanceaccountservice.validator.ValidUUID;
import com.beeswork.balanceaccountservice.vm.click.ClickVM;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Validated
@RestController
public class ClickController extends BaseController {

    private final ClickService clickService;
    private final FCMService FCMService;

    @Autowired
    public ClickController(ObjectMapper objectMapper, ModelMapper modelMapper, ClickService clickService,
                           FCMService FCMService) {
        super(objectMapper, modelMapper);
        this.clickService = clickService;
        this.FCMService = FCMService;
    }

    @PostMapping("/click")
    public ResponseEntity<String> click(@Valid @RequestBody ClickVM clickVM)
    throws SwipeNotFoundException, AccountInvalidException, MatchExistsException, JsonProcessingException,
           FirebaseMessagingException {

        List<FCMNotificationDTO> FCMNotificationDTOs = clickService.click(modelMapper.map(clickVM, ClickDTO.class));
        FCMService.sendNotifications(FCMNotificationDTOs);
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @GetMapping("/clicked/list")
    public ResponseEntity<String> listClicked(@RequestParam("clickedId") @ValidUUID String clickedId,
                                              @RequestParam("fetchedAt")
                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date fetchedAt)
    throws JsonProcessingException {

        return ResponseEntity.status(HttpStatus.OK)
                             .body(objectMapper.writeValueAsString(clickService.listClicked(clickedId, fetchedAt)));
    }


}
