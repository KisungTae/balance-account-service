package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.dto.profile.CardDTO;
import com.beeswork.balanceaccountservice.dto.profile.ProfileDTO;
import com.beeswork.balanceaccountservice.dto.profile.RecommendDTO;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.response.EmptyJsonResponse;
import com.beeswork.balanceaccountservice.service.profile.ProfileService;
import com.beeswork.balanceaccountservice.vm.account.*;
import com.beeswork.balanceaccountservice.vm.profile.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/profile")
public class ProfileController extends BaseController {

    private final ProfileService profileService;

    public ProfileController(ObjectMapper objectMapper,
                             ModelMapper modelMapper,
                             ProfileService profileService) {
        super(objectMapper, modelMapper);
        this.profileService = profileService;
    }

    @GetMapping
    public ResponseEntity<String> getProfile(@Valid @ModelAttribute AccountIdentityVM accountIdentityVM,
                                             BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        ProfileDTO profileDTO = profileService.getProfile(accountIdentityVM.getAccountId(),
                                                          accountIdentityVM.getIdentityToken());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(profileDTO));
    }

    @GetMapping("/card")
    public ResponseEntity<String> getCard(@Valid @ModelAttribute GetCardVM getCardVM,
                                          BindingResult bindingResult)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        CardDTO cardDTO = profileService.getCard(getCardVM.getAccountId(),
                                                 getCardVM.getIdentityToken(),
                                                 getCardVM.getSwipedId());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(cardDTO));
    }

    @PostMapping
    public ResponseEntity<String> saveProfile(@Valid @RequestBody SaveProfileVM saveProfileVM,
                                              BindingResult bindingResult)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) return super.fieldExceptionResponse(bindingResult);
        profileService.saveProfile(saveProfileVM.getAccountId(),
                                   saveProfileVM.getIdentityToken(),
                                   saveProfileVM.getName(),
                                   saveProfileVM.getBirth(),
                                   saveProfileVM.getAbout(),
                                   saveProfileVM.getHeight(),
                                   saveProfileVM.getGender());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @PostMapping("/about")
    public ResponseEntity<String> saveAbout(@Valid @RequestBody SaveAboutVM saveAboutVM,
                                            BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) return super.fieldExceptionResponse(bindingResult);
        profileService.saveAbout(saveAboutVM.getAccountId(),
                                 saveAboutVM.getIdentityToken(),
                                 saveAboutVM.getAbout(),
                                 saveAboutVM.getHeight());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @PostMapping("/location")
    public ResponseEntity<String> saveLocation(@Valid @RequestBody SaveLocationVM saveLocationVM,
                                               BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        profileService.saveLocation(saveLocationVM.getAccountId(),
                                    saveLocationVM.getIdentityToken(),
                                    saveLocationVM.getLatitude(),
                                    saveLocationVM.getLongitude(),
                                    saveLocationVM.getUpdatedAt());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @GetMapping("/recommend")
    public ResponseEntity<String> recommend(@Valid @ModelAttribute RecommendVM recommendVM, BindingResult bindingResult)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        RecommendDTO recommendDTO = profileService.recommend(recommendVM.getAccountId(),
                                                             recommendVM.getIdentityToken(),
                                                             recommendVM.getDistance(),
                                                             recommendVM.getMinAge(),
                                                             recommendVM.getMaxAge(),
                                                             recommendVM.isGender(),
                                                             recommendVM.getPageIndex());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(recommendDTO));
    }

    @PostMapping("/email")
    public ResponseEntity<String> saveEmail(@Valid @RequestBody SaveEmailVM saveEmailVM,
                                            BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) super.fieldExceptionResponse(bindingResult);
        profileService.saveEmail(saveEmailVM.getAccountId(), saveEmailVM.getIdentityToken(), saveEmailVM.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @GetMapping("/email")
    public ResponseEntity<String> getEmail(@Valid @ModelAttribute AccountIdentityVM accountIdentityVM,
                                           BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        String email = profileService.getEmail(accountIdentityVM.getAccountId(), accountIdentityVM.getIdentityToken());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(email));
    }
}
