package com.beeswork.balanceaccountservice.restcontroller;


import com.beeswork.balanceaccountservice.dto.account.AccountQuestionDTO;
import com.beeswork.balanceaccountservice.dto.account.AccountQuestionSaveDTO;
import com.beeswork.balanceaccountservice.dto.firebase.FCMTokenDTO;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.exception.question.QuestionNotFoundException;
import com.beeswork.balanceaccountservice.response.EmptyJsonResponse;
import com.beeswork.balanceaccountservice.service.account.AccountService;
import com.beeswork.balanceaccountservice.vm.account.AccountQuestionSaveVM;
import com.beeswork.balanceaccountservice.vm.account.AccountProfileSaveVM;
import com.beeswork.balanceaccountservice.vm.account.AccountFCMTokenSaveVM;
import com.beeswork.balanceaccountservice.vm.account.AccountLocationSaveVM;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/account")
public class AccountController extends BaseController {

    private final AccountService accountService;

    //    TODO: remove me
//    @Autowired
//    private FirebaseMessagingService firebaseMessagingService;

    @Autowired
    public AccountController(ObjectMapper objectMapper, ModelMapper modelMapper, AccountService accountService) {
        super(objectMapper, modelMapper);
        this.accountService = accountService;
    }

//    @PostMapping("/save")
//    public ResponseEntity<String> saveAccount(@Valid @RequestBody AccountVM accountVM,
//                                              BindingResult bindingResult)
//    throws JsonProcessingException, AccountNotFoundException, QuestionNotFoundException {
//
//        if (bindingResult.hasErrors()) return super.fieldExceptionResponse(bindingResult);
//        accountService.save(modelMapper.map(accountVM, AccountDTO.class));
//        return ResponseEntity.status(HttpStatus.OK).body("");
//    }

    @PostMapping("/profile/save")
    public ResponseEntity<String> saveProfile(@Valid @RequestBody AccountProfileSaveVM accountProfileSaveVM,
                                              BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) return super.fieldExceptionResponse(bindingResult);

        try {
            accountService.saveProfile(accountProfileSaveVM.getAccountId(),
                                       accountProfileSaveVM.getEmail(),
                                       accountProfileSaveVM.getName(),
                                       accountProfileSaveVM.getBirth(),
                                       accountProfileSaveVM.getAbout(),
                                       accountProfileSaveVM.getGender());
        } catch (ObjectOptimisticLockingFailureException exception) {
            accountService.saveProfile(accountProfileSaveVM.getAccountId(),
                                       accountProfileSaveVM.getEmail(),
                                       accountProfileSaveVM.getName(),
                                       accountProfileSaveVM.getBirth(),
                                       accountProfileSaveVM.getAbout(),
                                       accountProfileSaveVM.getGender());
        }

        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @PostMapping("/location/save")
    public ResponseEntity<String> saveLocation(@Valid @RequestBody AccountLocationSaveVM accountLocationSaveVM,
                                               BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();

        try {
            accountService.saveLocation(accountLocationSaveVM.getAccountId(),
                                        accountLocationSaveVM.getEmail(),
                                        accountLocationSaveVM.getLatitude(),
                                        accountLocationSaveVM.getLongitude());
        } catch (ObjectOptimisticLockingFailureException exception) {
            accountService.saveLocation(accountLocationSaveVM.getAccountId(),
                                        accountLocationSaveVM.getEmail(),
                                        accountLocationSaveVM.getLatitude(),
                                        accountLocationSaveVM.getLongitude());
        }

        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @PostMapping("/fcm/token/save")
    public ResponseEntity<String> saveFCMToken(@Valid @RequestBody AccountFCMTokenSaveVM accountFCMTokenSaveVM, BindingResult bindingResult)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();

        try {
            accountService.saveFCMToken(accountFCMTokenSaveVM.getAccountId(),
                                        accountFCMTokenSaveVM.getEmail(),
                                        accountFCMTokenSaveVM.getToken());
        } catch (ObjectOptimisticLockingFailureException exception) {
            accountService.saveFCMToken(accountFCMTokenSaveVM.getAccountId(),
                                        accountFCMTokenSaveVM.getEmail(),
                                        accountFCMTokenSaveVM.getToken());
        }

        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @PostMapping("/questions/save")
    public ResponseEntity<String> saveQuestions(@Valid @RequestBody AccountQuestionSaveVM accountQuestionSaveVM,
                                                BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) return super.fieldExceptionResponse(bindingResult);

        accountService.saveQuestions(accountQuestionSaveVM.getAccountId(),
                                     accountQuestionSaveVM.getEmail(),
                                     modelMapper.map(accountQuestionSaveVM.getAccountQuestionVMs(),
                                                     new TypeToken<List<AccountQuestionDTO>>() {}.getType()));
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }


}
