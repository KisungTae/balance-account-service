package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.dto.match.MatchDTO;
import com.beeswork.balanceaccountservice.dto.swipe.SwipeDTO;
import com.beeswork.balanceaccountservice.exception.account.AccountInvalidException;
import com.beeswork.balanceaccountservice.exception.match.MatchExistsException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeNotFoundException;
import com.beeswork.balanceaccountservice.service.match.MatchService;
import com.beeswork.balanceaccountservice.vm.match.SwipeVM;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/click")
    public ResponseEntity<String> balance(@Valid @RequestBody SwipeVM swipeVM)
    throws JsonProcessingException, SwipeNotFoundException, AccountInvalidException, MatchExistsException {

        MatchDTO matchDTO = matchService.click(modelMapper.map(swipeVM, SwipeDTO.class));
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(matchDTO));
    }


}
