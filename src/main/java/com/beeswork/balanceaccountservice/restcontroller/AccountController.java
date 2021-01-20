package com.beeswork.balanceaccountservice.restcontroller;


import com.beeswork.balanceaccountservice.dto.account.CardDTO;
import com.beeswork.balanceaccountservice.dto.account.PreRecommendDTO;
import com.beeswork.balanceaccountservice.dto.account.ProfileDTO;
import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
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
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    @PostMapping("/delete")
    public ResponseEntity<String> deleteAccount(@Valid @RequestBody AccountIdentityVM accountIdentityVM,
                                                BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        accountService.deleteAccount(accountIdentityVM.getAccountId(), accountIdentityVM.getIdentityToken());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @GetMapping("/profile")
    public ResponseEntity<String> getProfile(@Valid @ModelAttribute AccountIdentityVM accountIdentityVM,
                                             BindingResult bindingResult) throws JsonProcessingException {

        if (bindingResult.hasErrors()) throw new BadRequestException();

        ProfileDTO profileDTO = accountService.getProfile(accountIdentityVM.getAccountId(),
                                                          accountIdentityVM.getIdentityToken());

        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(profileDTO));
    }

    @PostMapping("/profile")
    public ResponseEntity<String> saveProfile(@Valid @RequestBody SaveProfileVM saveProfileVM,
                                              BindingResult bindingResult)
    throws JsonProcessingException {

        if (bindingResult.hasErrors()) return super.fieldExceptionResponse(bindingResult);

        accountService.saveProfile(saveProfileVM.getAccountId(),
                                   saveProfileVM.getIdentityToken(),
                                   saveProfileVM.getName(),
                                   saveProfileVM.getBirth(),
                                   saveProfileVM.getAbout(),
                                   saveProfileVM.getHeight(),
                                   saveProfileVM.getGender());

        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @PostMapping("/about")
    public ResponseEntity<String> saveAbout(@RequestBody SaveAboutVM saveAboutVM) throws JsonProcessingException {

        accountService.saveAbout(saveAboutVM.getAccountId(),
                                 saveAboutVM.getIdentityToken(),
                                 saveAboutVM.getAbout(),
                                 saveAboutVM.getHeight());

        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @PostMapping("/location")
    public ResponseEntity<String> saveLocation(@Valid @RequestBody SaveLocationVM saveLocationVM,
                                               BindingResult bindingResult) throws JsonProcessingException {

        if (bindingResult.hasErrors()) throw new BadRequestException();

        accountService.saveLocation(saveLocationVM.getAccountId(),
                                    saveLocationVM.getIdentityToken(),
                                    saveLocationVM.getLatitude(),
                                    saveLocationVM.getLongitude(),
                                    saveLocationVM.getUpdatedAt());

        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @PostMapping("/fcm/token")
    public ResponseEntity<String> saveFCMToken(@Valid @RequestBody SaveFCMTokenVM saveFCMTokenVM,
                                               BindingResult bindingResult)
    throws JsonProcessingException {

        if (bindingResult.hasErrors()) throw new BadRequestException();

        accountService.saveFCMToken(saveFCMTokenVM.getAccountId(),
                                    saveFCMTokenVM.getIdentityToken(),
                                    saveFCMTokenVM.getToken());

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
    @GetMapping("/recommend")
    public ResponseEntity<String> recommend(@Valid @ModelAttribute RecommendVM recommendVM,
                                            BindingResult bindingResult)
    throws JsonProcessingException {

        if (bindingResult.hasErrors()) throw new BadRequestException();

        PreRecommendDTO preRecommendDTO = accountService.preRecommend(recommendVM.getAccountId(),
                                                                      recommendVM.getIdentityToken(),
                                                                      recommendVM.getLatitude(),
                                                                      recommendVM.getLongitude(),
                                                                      recommendVM.getLocationUpdatedAt(),
                                                                      recommendVM.isReset());

        List<CardDTO> cardDTOs = accountService.recommend(recommendVM.getDistance(),
                                                          recommendVM.getMinAge(),
                                                          recommendVM.getMaxAge(),
                                                          recommendVM.isGender(),
                                                          preRecommendDTO.getLocation(),
                                                          preRecommendDTO.getIndex());

        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(cardDTOs));
    }


}
