package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.dto.swipe.BalanceGameDTO;
import com.beeswork.balanceaccountservice.dto.swipe.SwipeDTO;
import com.beeswork.balanceaccountservice.exception.account.AccountBlockedException;
import com.beeswork.balanceaccountservice.exception.account.AccountInvalidException;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.exception.account.AccountShortOfPointException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeClickedExistsException;
import com.beeswork.balanceaccountservice.service.swipe.SwipeService;
import com.beeswork.balanceaccountservice.vm.swipe.SwipeVM;
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
           AccountShortOfPointException, AccountBlockedException {

        if (bindingResult.hasErrors()) return super.fieldExceptionResponse(bindingResult);
        BalanceGameDTO balanceGameDTO = swipeService.swipe(modelMapper.map(swipeVM, SwipeDTO.class));
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(balanceGameDTO));
    }
}
