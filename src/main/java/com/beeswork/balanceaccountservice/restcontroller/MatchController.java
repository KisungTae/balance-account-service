package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.service.match.MatchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> listMatches(@RequestParam("accountId") String accountId) {

        return null;
    }

    @PostMapping("/unmatch")
    public ResponseEntity<String> unmatch() {
        return null;
    }

}
