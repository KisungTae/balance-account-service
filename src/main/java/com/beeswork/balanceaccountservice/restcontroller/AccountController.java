package com.beeswork.balanceaccountservice.restcontroller;


import com.beeswork.balanceaccountservice.dto.account.AccountQuestionDTO;
import com.beeswork.balanceaccountservice.dto.account.CardDTO;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.response.EmptyJsonResponse;
import com.beeswork.balanceaccountservice.service.account.AccountService;
import com.beeswork.balanceaccountservice.vm.account.*;
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
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/account")
public class AccountController extends BaseController {

    private final AccountService accountService;

    @Autowired
    public AccountController(ObjectMapper objectMapper, ModelMapper modelMapper, AccountService accountService) {
        super(objectMapper, modelMapper);
        this.accountService = accountService;
    }

    @PostMapping("/profile")
    public ResponseEntity<String> saveProfile(@Valid @RequestBody SaveProfileVM saveProfileVM,
                                              BindingResult bindingResult)
    throws JsonProcessingException {

        if (bindingResult.hasErrors()) return super.fieldExceptionResponse(bindingResult);

        try {
            accountService.saveProfile(saveProfileVM.getAccountId(),
                                       saveProfileVM.getEmail(),
                                       saveProfileVM.getName(),
                                       saveProfileVM.getBirth(),
                                       saveProfileVM.getAbout(),
                                       saveProfileVM.getGender());
        } catch (ObjectOptimisticLockingFailureException exception) {
            accountService.saveProfile(saveProfileVM.getAccountId(),
                                       saveProfileVM.getEmail(),
                                       saveProfileVM.getName(),
                                       saveProfileVM.getBirth(),
                                       saveProfileVM.getAbout(),
                                       saveProfileVM.getGender());
        }

        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @PostMapping("/location")
    public ResponseEntity<String> saveLocation(@Valid @RequestBody SaveLocationVM saveLocationVM,
                                               BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();

        try {
            accountService.saveLocation(saveLocationVM.getAccountId(),
                                        saveLocationVM.getEmail(),
                                        saveLocationVM.getLatitude(),
                                        saveLocationVM.getLongitude());
        } catch (ObjectOptimisticLockingFailureException exception) {
            accountService.saveLocation(saveLocationVM.getAccountId(),
                                        saveLocationVM.getEmail(),
                                        saveLocationVM.getLatitude(),
                                        saveLocationVM.getLongitude());
        }

        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @PostMapping("/fcm/token")
    public ResponseEntity<String> saveFCMToken(@Valid @RequestBody SaveFCMTokenVM SaveFCMTokenVM, BindingResult bindingResult)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();

        try {
            accountService.saveFCMToken(SaveFCMTokenVM.getAccountId(),
                                        SaveFCMTokenVM.getEmail(),
                                        SaveFCMTokenVM.getToken());
        } catch (ObjectOptimisticLockingFailureException exception) {
            accountService.saveFCMToken(SaveFCMTokenVM.getAccountId(),
                                        SaveFCMTokenVM.getEmail(),
                                        SaveFCMTokenVM.getToken());
        }

        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @PostMapping("/question")
    public ResponseEntity<String> saveAccountQuestions(@Valid @RequestBody SaveAccountQuestionVM saveAccountQuestionVM,
                                                BindingResult bindingResult)
    throws JsonProcessingException {

        if (bindingResult.hasErrors()) throw new BadRequestException();

        accountService.saveQuestions(saveAccountQuestionVM.getAccountId(),
                                     saveAccountQuestionVM.getEmail(),
                                     modelMapper.map(saveAccountQuestionVM.getAccountQuestionVMs(),
                                                     new TypeToken<List<AccountQuestionDTO>>() {}.getType()));

        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @GetMapping("/recommend")
    public ResponseEntity<String> recommend(@Valid @ModelAttribute RecommendVM recommendVM, BindingResult bindingResult)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();

        List<CardDTO> cardDTOs = accountService.recommend(recommendVM.getAccountId(),
                                                          recommendVM.getEmail(),
                                                          recommendVM.getDistance(),
                                                          recommendVM.getMinAge(),
                                                          recommendVM.getMaxAge(),
                                                          recommendVM.isGender(),
                                                          recommendVM.getLatitude(),
                                                          recommendVM.getLongitude());

        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(cardDTOs));
    }


}
