package com.beeswork.balanceaccountservice.restcontroller;


import com.beeswork.balanceaccountservice.dto.click.ClickDTO;
import com.beeswork.balanceaccountservice.exception.account.AccountInvalidException;
import com.beeswork.balanceaccountservice.exception.match.MatchExistsException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeNotFoundException;
import com.beeswork.balanceaccountservice.response.EmptyJsonResponse;
import com.beeswork.balanceaccountservice.service.click.ClickService;
import com.beeswork.balanceaccountservice.validator.ValidUUID;
import com.beeswork.balanceaccountservice.vm.click.ClickVM;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class ClickController extends BaseController {

    private final ClickService clickService;

    @Autowired
    public ClickController(ObjectMapper objectMapper, ModelMapper modelMapper, ClickService clickService) {
        super(objectMapper, modelMapper);
        this.clickService = clickService;
    }

    @PostMapping("/click")
    public ResponseEntity<String> click(@Valid @RequestBody ClickVM clickVM)
    throws SwipeNotFoundException, AccountInvalidException, MatchExistsException, JsonProcessingException {

        clickService.click(modelMapper.map(clickVM, ClickDTO.class));
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }


    @GetMapping("/click/list")
    public ResponseEntity<String> listClicks(@RequestParam("accountId") @ValidUUID String accountId) throws JsonProcessingException {
//
//        SwipeListDTO swipeListDTO = click.listSwipes(accountId);
//        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(swipeListDTO));
        return null;
    }


    @GetMapping("/clicked/list")
    public ResponseEntity<String> listClicked(@RequestParam("accountId") @ValidUUID String accountId) throws JsonProcessingException {

        clickService.listClick(accountId);
        //
//        SwipeListDTO swipeListDTO = click.listSwipes(accountId);
//        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(swipeListDTO));

        return null;
    }


}
