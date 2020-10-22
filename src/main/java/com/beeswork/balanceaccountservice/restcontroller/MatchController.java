package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.dto.match.UnmatchDTO;
import com.beeswork.balanceaccountservice.exception.account.AccountInvalidException;
import com.beeswork.balanceaccountservice.response.EmptyJsonResponse;
import com.beeswork.balanceaccountservice.service.match.MatchService;
import com.beeswork.balanceaccountservice.validator.ValidUUID;
import com.beeswork.balanceaccountservice.vm.match.UnmatchVM;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;

@Validated
@RestController
public class MatchController extends BaseController {

    private final MatchService matchService;

    @Autowired
    public MatchController(ObjectMapper objectMapper, ModelMapper modelMapper, MatchService matchService) {
        super(objectMapper, modelMapper);
        this.matchService = matchService;
    }

    @GetMapping("/match/list")
    public ResponseEntity<String> listMatches(@RequestParam("matcherId") @ValidUUID String matcherId,
                                              @RequestParam("email") String email,
                                              @RequestParam("fetchedAt")
                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date fetchedAt)
    throws JsonProcessingException, AccountInvalidException {

        return ResponseEntity.status(HttpStatus.OK)
                             .body(objectMapper.writeValueAsString(matchService.listMatches(matcherId, email, fetchedAt)));
    }

    @PostMapping("/unmatch")
    public ResponseEntity<String> unmatch(@Valid @RequestBody UnmatchVM unmatchVM,
                                          BindingResult bindingResult)
    throws JsonProcessingException, AccountInvalidException {

        if (bindingResult.hasErrors()) return super.fieldExceptionResponse(bindingResult);
        matchService.unmatch(modelMapper.map(unmatchVM, UnmatchDTO.class));
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

}
