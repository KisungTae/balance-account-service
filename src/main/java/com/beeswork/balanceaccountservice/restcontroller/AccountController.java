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
import com.google.firebase.auth.FirebaseAuthException;
import org.modelmapper.ModelMapper;
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

    @Autowired
    public AccountController(ObjectMapper objectMapper, ModelMapper modelMapper, AccountService accountService) {
        super(objectMapper, modelMapper);
        this.accountService = accountService;
    }

    @GetMapping("/questions")
    public ResponseEntity<String> getQuestions(@Valid @ModelAttribute AccountIdentityVM accountIdentityVM,
                                               BindingResult bindingResult) throws JsonProcessingException {

        if (bindingResult.hasErrors()) throw new BadRequestException();

        List<QuestionDTO> questionDTOs = accountService.getQuestions(accountIdentityVM.getAccountId(),
                                                                     accountIdentityVM.getIdentityToken());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(questionDTOs));
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

        try {
            accountService.saveProfile(saveProfileVM.getAccountId(),
                                       saveProfileVM.getIdentityToken(),
                                       saveProfileVM.getName(),
                                       saveProfileVM.getBirth(),
                                       saveProfileVM.getAbout(),
                                       saveProfileVM.getHeight(),
                                       saveProfileVM.getGender());
        } catch (ObjectOptimisticLockingFailureException exception) {
            accountService.saveProfile(saveProfileVM.getAccountId(),
                                       saveProfileVM.getIdentityToken(),
                                       saveProfileVM.getName(),
                                       saveProfileVM.getBirth(),
                                       saveProfileVM.getAbout(),
                                       saveProfileVM.getHeight(),
                                       saveProfileVM.getGender());
        }

        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @PostMapping("/about")
    public ResponseEntity<String> saveAbout(@RequestBody SaveAboutVM saveAboutVM) throws JsonProcessingException {

        try {
            accountService.saveAbout(saveAboutVM.getAccountId(),
                                     saveAboutVM.getIdentityToken(),
                                     saveAboutVM.getAbout(),
                                     saveAboutVM.getHeight());
        } catch (ObjectOptimisticLockingFailureException exception) {
            accountService.saveAbout(saveAboutVM.getAccountId(),
                                     saveAboutVM.getIdentityToken(),
                                     saveAboutVM.getAbout(),
                                     saveAboutVM.getHeight());
        }

        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @PostMapping("/location")
    public ResponseEntity<String> saveLocation(@Valid @RequestBody SaveLocationVM saveLocationVM,
                                               BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();

        System.out.println(bindingResult.getAllErrors());

        try {
            accountService.saveLocation(saveLocationVM.getAccountId(),
                                        saveLocationVM.getIdentityToken(),
                                        saveLocationVM.getLatitude(),
                                        saveLocationVM.getLongitude(),
                                        saveLocationVM.getLocationUpdatedAt());
        } catch (ObjectOptimisticLockingFailureException exception) {
            accountService.saveLocation(saveLocationVM.getAccountId(),
                                        saveLocationVM.getIdentityToken(),
                                        saveLocationVM.getLatitude(),
                                        saveLocationVM.getLongitude(),
                                        saveLocationVM.getLocationUpdatedAt());
        }

        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @PostMapping("/fcm/token")
    public ResponseEntity<String> saveFCMToken(@Valid @RequestBody SaveFCMTokenVM saveFCMTokenVM,
                                               BindingResult bindingResult)
    throws JsonProcessingException {

        if (bindingResult.hasErrors()) throw new BadRequestException();

        try {
            accountService.saveFCMToken(saveFCMTokenVM.getAccountId(),
                                        saveFCMTokenVM.getIdentityToken(),
                                        saveFCMTokenVM.getToken());
        } catch (ObjectOptimisticLockingFailureException exception) {
            accountService.saveFCMToken(saveFCMTokenVM.getAccountId(),
                                        saveFCMTokenVM.getIdentityToken(),
                                        saveFCMTokenVM.getToken());
        }

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

    @GetMapping("/recommend")
    public ResponseEntity<String> recommend(@Valid @ModelAttribute RecommendVM recommendVM,
                                            BindingResult bindingResult)
    throws JsonProcessingException {

        if (bindingResult.hasErrors()) throw new BadRequestException();

        PreRecommendDTO preRecommendDTO;
        try {
            preRecommendDTO = accountService.preRecommend(recommendVM.getAccountId(), recommendVM.getIdentityToken(),
                                                          recommendVM.getLatitude(), recommendVM.getLongitude(),
                                                          recommendVM.getLocationUpdatedAt(), recommendVM.isReset());
        } catch (ObjectOptimisticLockingFailureException exception) {
            preRecommendDTO = accountService.preRecommend(recommendVM.getAccountId(), recommendVM.getIdentityToken(),
                                                          recommendVM.getLatitude(), recommendVM.getLongitude(),
                                                          recommendVM.getLocationUpdatedAt(), recommendVM.isReset());
        }

        List<CardDTO> cardDTOs = accountService.recommend(recommendVM.getDistance(),
                                                          recommendVM.getMinAge(),
                                                          recommendVM.getMaxAge(),
                                                          recommendVM.isGender(),
                                                          preRecommendDTO.getLocation(),
                                                          preRecommendDTO.getIndex());

        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(cardDTOs));
    }


}
