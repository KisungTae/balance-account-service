package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.dto.match.BalanceGameDTO;
import com.beeswork.balanceaccountservice.dto.swipe.SwipeDTO;
import com.beeswork.balanceaccountservice.dto.swipe.SwipeListDTO;
import com.beeswork.balanceaccountservice.exception.account.AccountInvalidException;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.exception.account.AccountShortOfPointException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeClickedExistsException;
import com.beeswork.balanceaccountservice.service.swipe.SwipeService;
import com.beeswork.balanceaccountservice.validator.ValidUUID;
import com.beeswork.balanceaccountservice.vm.match.SwipeVM;
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
@RequestMapping("/swipe")
public class SwipeController extends BaseController {

    private final SwipeService swipeService;

    @Autowired
    public SwipeController(ObjectMapper objectMapper, ModelMapper modelMapper, SwipeService swipeService) {
        super(objectMapper, modelMapper);
        this.swipeService = swipeService;
    }

    @PostMapping
    public ResponseEntity<String> swipe(@Valid @RequestBody SwipeVM swipeVM, BindingResult bindingResult)
    throws AccountNotFoundException, AccountInvalidException, SwipeClickedExistsException, JsonProcessingException,
           AccountShortOfPointException {

        if (bindingResult.hasErrors()) return super.fieldErrorsResponse(bindingResult);
        BalanceGameDTO balanceGameDTO = swipeService.swipe(modelMapper.map(swipeVM, SwipeDTO.class));
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(balanceGameDTO));
    }

    @GetMapping("/list")
    public ResponseEntity<String> listSwipes(@RequestParam("account_id") @ValidUUID String accountId) throws JsonProcessingException {

        SwipeListDTO swipeListDTO = swipeService.listSwipes(accountId);
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(swipeListDTO));
    }
}
