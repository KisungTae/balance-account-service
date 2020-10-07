package com.beeswork.balanceaccountservice.restcontroller;


import com.beeswork.balanceaccountservice.dto.account.AccountDTO;
import com.beeswork.balanceaccountservice.dto.account.AccountQuestionSaveDTO;
import com.beeswork.balanceaccountservice.dto.account.FirebaseMessagingTokenDTO;
import com.beeswork.balanceaccountservice.dto.account.LocationDTO;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.exception.question.QuestionNotFoundException;
import com.beeswork.balanceaccountservice.response.EmptyJsonResponse;
import com.beeswork.balanceaccountservice.service.account.AccountService;
import com.beeswork.balanceaccountservice.vm.account.AccountQuestionSaveVM;
import com.beeswork.balanceaccountservice.vm.account.AccountVM;
import com.beeswork.balanceaccountservice.vm.account.FirebaseMessageTokenVM;
import com.beeswork.balanceaccountservice.vm.account.LocationVM;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

@RestController
@RequestMapping("/account")
public class AccountController extends BaseController {

    private final AccountService accountService;

    @Autowired
    public AccountController(ObjectMapper objectMapper, ModelMapper modelMapper, AccountService accountService) {
        super(objectMapper, modelMapper);
        this.accountService = accountService;
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveAccount(@Valid @RequestBody AccountVM accountVM,
                                              BindingResult bindingResult)
    throws JsonProcessingException, AccountNotFoundException, QuestionNotFoundException {

        if (bindingResult.hasErrors()) return super.fieldErrorsResponse(bindingResult);
        accountService.save(modelMapper.map(accountVM, AccountDTO.class));
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @PostMapping("/profile/save")
    public ResponseEntity<String> saveProfile(@Valid @RequestBody AccountVM accountVM,
                                              BindingResult bindingResult)
    throws JsonProcessingException, AccountNotFoundException {

        if (bindingResult.hasErrors()) return super.fieldErrorsResponse(bindingResult);
        accountService.saveProfile(modelMapper.map(accountVM, AccountDTO.class));
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @PostMapping("/questions/save")
    public ResponseEntity<String> saveQuestions(@Valid @RequestBody AccountQuestionSaveVM accountQuestionSaveVM,
                                                BindingResult bindingResult)
    throws QuestionNotFoundException, AccountNotFoundException, JsonProcessingException {

        if (bindingResult.hasErrors()) return super.fieldErrorsResponse(bindingResult);
        accountService.saveQuestions(modelMapper.map(accountQuestionSaveVM, AccountQuestionSaveDTO.class));
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @PostMapping("/location/save")
    public ResponseEntity<String> saveLocation(@Valid @RequestBody LocationVM locationVM,
                                               BindingResult bindingResult)
    throws JsonProcessingException, AccountNotFoundException {

        if (bindingResult.hasErrors()) return super.fieldErrorsResponse(bindingResult);
        accountService.saveLocation(modelMapper.map(locationVM, LocationDTO.class));
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @PostMapping("/message-token/save")
    public ResponseEntity<String> saveFirebaseMessagingToken(
            @Valid @RequestBody FirebaseMessageTokenVM firebaseMessageTokenVM,
            BindingResult bindingResult)
    throws JsonProcessingException, AccountNotFoundException {

        if (bindingResult.hasErrors()) return super.fieldErrorsResponse(bindingResult);
        accountService.saveFirebaseMessagingToken(
                modelMapper.map(firebaseMessageTokenVM, FirebaseMessagingTokenDTO.class));
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    //  TODO: remove me
    @PostMapping("send-message/{token}")
    public void firebaseMessaging(@PathVariable("token") String token) throws IOException, FirebaseMessagingException {
        FileInputStream serviceAccount = new FileInputStream(
                "/Users/kisungtae/Documents/intellijSpringProjects/balance-896d6-firebase-adminsdk-sppjt-13d87a9365.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://balance-896d6.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);

        Random random = new Random();
        Message message = Message.builder()
                                 .putData("message", "this is test message from server " + random.nextInt())
                                 .setToken(token)
                                 .build();

        String response = FirebaseMessaging.getInstance().send(message);

        System.out.println("response: " + response);
    }

}
