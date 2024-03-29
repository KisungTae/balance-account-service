package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.dto.profile.CardDTO;
import com.beeswork.balanceaccountservice.dto.profile.ProfileDTO;
import com.beeswork.balanceaccountservice.dto.profile.RecommendDTO;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.response.EmptyJsonResponse;
import com.beeswork.balanceaccountservice.service.profile.ProfileService;
import com.beeswork.balanceaccountservice.service.report.ReportService;
import com.beeswork.balanceaccountservice.vm.account.*;
import com.beeswork.balanceaccountservice.vm.profile.*;
import com.beeswork.balanceaccountservice.vm.report.ReportVM;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@RestController
@RequestMapping("/profile")
public class ProfileController extends BaseController {

    private final ProfileService profileService;
    private final ReportService  reportService;

    public ProfileController(ObjectMapper objectMapper,
                             ModelMapper modelMapper,
                             ProfileService profileService,
                             ReportService reportService) {
        super(objectMapper, modelMapper);
        this.profileService = profileService;
        this.reportService = reportService;
    }

    @GetMapping
    public ResponseEntity<String> getProfile(Principal principal) throws JsonProcessingException {
        ProfileDTO profileDTO = profileService.getProfile(getAccountIdFrom(principal));
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(profileDTO));
    }

    @GetMapping("/card")
    public ResponseEntity<String> getCard(@Valid @ModelAttribute GetCardVM getCardVM,
                                          BindingResult bindingResult,
                                          Principal principal)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        CardDTO cardDTO = profileService.getCard(getAccountIdFrom(principal), getCardVM.getSwipedId());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(cardDTO));
    }

    @PostMapping
    public ResponseEntity<String> saveProfile(@Valid @RequestBody SaveProfileVM saveProfileVM,
                                              BindingResult bindingResult,
                                              Principal principal)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) return super.fieldExceptionResponse(bindingResult);
        profileService.saveProfile(getAccountIdFrom(principal),
                                   saveProfileVM.getName(),
                                   saveProfileVM.getBirthDate(),
                                   saveProfileVM.getAbout(),
                                   saveProfileVM.getHeight(),
                                   saveProfileVM.getGender(),
                                   saveProfileVM.getLatitude(),
                                   saveProfileVM.getLongitude());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @PostMapping("/bio")
    public ResponseEntity<String> saveBio(@Valid @RequestBody SaveBioVM saveBioVM,
                                            BindingResult bindingResult,
                                            Principal principal) throws JsonProcessingException {
        if (bindingResult.hasErrors()) return super.fieldExceptionResponse(bindingResult);
        profileService.saveBio(getAccountIdFrom(principal), saveBioVM.getAbout(), saveBioVM.getHeight());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @PostMapping("/location")
    public ResponseEntity<String> saveLocation(@Valid @RequestBody SaveLocationVM saveLocationVM,
                                               BindingResult bindingResult,
                                               Principal principal) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        profileService.saveLocation(getAccountIdFrom(principal),
                                    saveLocationVM.getLatitude(),
                                    saveLocationVM.getLongitude(),
                                    saveLocationVM.getUpdatedAt());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @GetMapping("/recommend")
    public ResponseEntity<String> recommend(@Valid @ModelAttribute RecommendVM recommendVM,
                                            BindingResult bindingResult,
                                            Principal principal)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        List<CardDTO> cardDTOs = profileService.recommend(getAccountIdFrom(principal),
                                                          recommendVM.getDistance(),
                                                          recommendVM.getMinAge(),
                                                          recommendVM.getMaxAge(),
                                                          recommendVM.isGender(),
                                                          recommendVM.getPageIndex());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(cardDTOs));
    }

    @PostMapping("/email")
    public ResponseEntity<String> saveEmail(@Valid @RequestBody SaveEmailVM saveEmailVM,
                                            BindingResult bindingResult,
                                            Principal principal) throws JsonProcessingException {
        if (bindingResult.hasErrors()) super.fieldExceptionResponse(bindingResult);
        profileService.saveEmail(getAccountIdFrom(principal), saveEmailVM.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @GetMapping("/email")
    public ResponseEntity<String> getEmail(BindingResult bindingResult, Principal principal) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        String email = profileService.getEmail(getAccountIdFrom(principal));
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(email));
    }

    @PostMapping("/report")
    public ResponseEntity<String> reportProfile(@Valid @RequestBody ReportVM reportVM,
                                                BindingResult bindingResult,
                                                Principal principal) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        reportService.createReport(getAccountIdFrom(principal),
                                   reportVM.getReportedId(),
                                   reportVM.getReportReasonId(),
                                   reportVM.getDescription());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }
}
