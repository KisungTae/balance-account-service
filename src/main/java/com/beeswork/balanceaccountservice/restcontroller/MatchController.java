package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.dto.match.ListMatchDTO;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.response.EmptyJsonResponse;
import com.beeswork.balanceaccountservice.service.match.MatchService;
import com.beeswork.balanceaccountservice.vm.match.ListMatchesVM;
import com.beeswork.balanceaccountservice.vm.match.UnmatchVM;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    public ResponseEntity<String> listMatches(@Valid @ModelAttribute ListMatchesVM listMatchesVM,
                                              BindingResult bindingResult)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        ListMatchDTO listMatchDTO = matchService.listMatches(listMatchesVM.getAccountId(),
                                                             listMatchesVM.getIdentityToken(),
                                                             listMatchesVM.getLastFetchedAccountUpdatedAt(),
                                                             listMatchesVM.getLastFetchedMatchUpdatedAt(),
                                                             listMatchesVM.getLastFetchedChatMessageCreatedAt());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(listMatchDTO));
    }

    @PostMapping("/unmatch")
    public ResponseEntity<String> unmatch(@Valid @RequestBody UnmatchVM unmatchVM, BindingResult bindingResult)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        matchService.unmatch(unmatchVM.getAccountId(), unmatchVM.getIdentityToken(), unmatchVM.getUnmatchedId());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

}
