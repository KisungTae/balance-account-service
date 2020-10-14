package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.dto.match.UnmatchDTO;
import com.beeswork.balanceaccountservice.exception.account.AccountInvalidException;
import com.beeswork.balanceaccountservice.projection.MatchProjection;
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
import java.util.List;

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
                                              @RequestParam("fetchedAt")
                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fetchedAt)
    throws JsonProcessingException {

        List<MatchProjection> projections = matchService.listMatches(matcherId, fetchedAt);
        return ResponseEntity.status(HttpStatus.OK)
                             .body(objectMapper.writeValueAsString(matchService.listMatches(matcherId, fetchedAt)));
    }

    @PostMapping("/unmatch")
    public ResponseEntity<String> unmatch(@Valid @RequestBody UnmatchVM unmatchVM,
                                          BindingResult bindingResult)
    throws JsonProcessingException, AccountInvalidException {

        if (bindingResult.hasErrors()) return super.fieldErrorsResponse(bindingResult);
        matchService.unmatch(modelMapper.map(unmatchVM, UnmatchDTO.class));
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

}
