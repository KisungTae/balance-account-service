package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.dto.account.CardDTO;
import com.beeswork.balanceaccountservice.dto.account.PreRecommendDTO;
import com.beeswork.balanceaccountservice.dto.account.ProfileDTO;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.response.EmptyJsonResponse;
import com.beeswork.balanceaccountservice.service.profile.ProfileService;
import com.beeswork.balanceaccountservice.vm.account.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

//    @GetMapping("/profile")
//    public ResponseEntity<String> getProfile(@Valid @ModelAttribute AccountIdentityVM accountIdentityVM,
//                                             BindingResult bindingResult) throws JsonProcessingException {
//
//        if (bindingResult.hasErrors()) throw new BadRequestException();
//
//        ProfileDTO profileDTO = profileService.getProfile(accountIdentityVM.getAccountId(),
//                                                          accountIdentityVM.getIdentityToken());
//
//        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(profileDTO));
//    }

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



//    @PostMapping("/about")
//    public ResponseEntity<String> saveAbout(@Valid @RequestBody SaveAboutVM saveAboutVM,
//                                            BindingResult bindingResult) throws JsonProcessingException {
//        if (bindingResult.hasErrors()) throw new BadRequestException();
//        profileService.saveAbout(saveAboutVM.getAccountId(),
//                                 saveAboutVM.getIdentityToken(),
//                                 saveAboutVM.getAbout(),
//                                 saveAboutVM.getHeight());
//
//        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
//    }
//
//    @PostMapping("/location")
//    public ResponseEntity<String> saveLocation(@Valid @RequestBody SaveLocationVM saveLocationVM,
//                                               BindingResult bindingResult) throws JsonProcessingException {
//
//        if (bindingResult.hasErrors()) throw new BadRequestException();
//
//        profileService.saveLocation(saveLocationVM.getAccountId(),
//                                    saveLocationVM.getIdentityToken(),
//                                    saveLocationVM.getLatitude(),
//                                    saveLocationVM.getLongitude(),
//                                    saveLocationVM.getUpdatedAt());
//
//        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
//    }
//
//    @GetMapping("/recommend")
//    public ResponseEntity<String> recommend(@Valid @ModelAttribute RecommendVM recommendVM,
//                                            BindingResult bindingResult)
//    throws JsonProcessingException {
//
//        if (bindingResult.hasErrors()) throw new BadRequestException();
//
//        PreRecommendDTO preRecommendDTO = profileService.preRecommend(recommendVM.getAccountId(),
//                                                                      recommendVM.getIdentityToken(),
//                                                                      recommendVM.getLatitude(),
//                                                                      recommendVM.getLongitude(),
//                                                                      recommendVM.getLocationUpdatedAt(),
//                                                                      recommendVM.isReset());
//
//        List<CardDTO> cardDTOs = profileService.recommend(recommendVM.getDistance(),
//                                                          recommendVM.getMinAge(),
//                                                          recommendVM.getMaxAge(),
//                                                          recommendVM.isGender(),
//                                                          preRecommendDTO.getLocation(),
//                                                          preRecommendDTO.getIndex());
//
//        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(cardDTOs));
//    }
}
