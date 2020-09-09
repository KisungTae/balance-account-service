package com.beeswork.balanceaccountservice.restcontroller;


import com.beeswork.balanceaccountservice.dto.account.AccountDTO;
import com.beeswork.balanceaccountservice.dto.account.AccountQuestionSaveDTO;
import com.beeswork.balanceaccountservice.dto.account.LocationDTO;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.exception.question.QuestionNotFoundException;
import com.beeswork.balanceaccountservice.service.account.AccountService;
import com.beeswork.balanceaccountservice.vm.account.AccountQuestionSaveVM;
import com.beeswork.balanceaccountservice.vm.account.AccountVM;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/account")
public class AccountController extends BaseController {

    private final AccountService accountService;

    @Autowired
    public AccountController(ObjectMapper objectMapper, ModelMapper modelMapper, AccountService accountService) {
        super(objectMapper, modelMapper);
        this.accountService = accountService;
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveAccount(@Valid @RequestBody AccountVM accountVM,
                                              BindingResult bindingResult)
    throws JsonProcessingException, AccountNotFoundException, QuestionNotFoundException {

        if (bindingResult.hasErrors()) return super.fieldErrorsResponse(bindingResult);
        accountService.save(modelMapper.map(accountVM, AccountDTO.class));
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @PostMapping("/profile/save")
    public ResponseEntity<String> saveProfile(@Valid @RequestBody AccountVM accountVM,
                                              BindingResult bindingResult)
    throws JsonProcessingException, AccountNotFoundException {

        if (bindingResult.hasErrors()) return super.fieldErrorsResponse(bindingResult);
        accountService.saveProfile(modelMapper.map(accountVM, AccountDTO.class));
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @PostMapping("/questions/save")
    public ResponseEntity<String> saveQuestions(@Valid @RequestBody AccountQuestionSaveVM accountQuestionSaveVM,
                                                BindingResult bindingResult)
    throws QuestionNotFoundException, AccountNotFoundException, JsonProcessingException {

        if (bindingResult.hasErrors()) return super.fieldErrorsResponse(bindingResult);
        accountService.saveQuestions(modelMapper.map(accountQuestionSaveVM, AccountQuestionSaveDTO.class));
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @PostMapping("/location/save")
    public ResponseEntity<String> saveLocation(@Valid @RequestBody LocationDTO locationDTO,
                                               BindingResult bindingResult)
    throws JsonProcessingException, AccountNotFoundException {

        if (bindingResult.hasErrors()) return super.fieldErrorsResponse(bindingResult);
        accountService.saveLocation(locationDTO);
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

}
