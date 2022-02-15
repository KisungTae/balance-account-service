package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.dto.match.ListMatchesDTO;
import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.response.EmptyJsonResponse;
import com.beeswork.balanceaccountservice.service.match.MatchService;
import com.beeswork.balanceaccountservice.vm.match.ListMatchesVM;
import com.beeswork.balanceaccountservice.vm.match.UnmatchVM;
import com.beeswork.balanceaccountservice.vm.report.ReportVM;
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
import java.security.Principal;
import java.util.List;

@Validated
@RestController
@RequestMapping("/match")
public class MatchController extends BaseController {
    private final MatchService matchService;

    @Autowired
    public MatchController(ObjectMapper objectMapper, ModelMapper modelMapper, MatchService matchService) {
        super(objectMapper, modelMapper);
        this.matchService = matchService;
    }

    @GetMapping("/list")
    public ResponseEntity<String> listMatches(@Valid @ModelAttribute ListMatchesVM listMatchesVM,
                                              BindingResult bindingResult,
                                              Principal principal)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        ListMatchesDTO listMatchesDTO = matchService.listMatches(getAccountIdFrom(principal), listMatchesVM.getFetchedAt());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(listMatchesDTO));
    }

    @PostMapping("/unmatch")
    public ResponseEntity<String> unmatch(@Valid @RequestBody UnmatchVM unmatchVM,
                                          BindingResult bindingResult,
                                          Principal principal)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        matchService.unmatch(getAccountIdFrom(principal), unmatchVM.getSwipedId());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @PostMapping("/report")
    public ResponseEntity<String> reportMatch(@Valid @RequestBody ReportVM reportVM,
                                              BindingResult bindingResult,
                                              Principal principal) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        matchService.reportMatch(getAccountIdFrom(principal), reportVM.getReportedId(), reportVM.getReportReasonId(), reportVM.getDescription());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

}
