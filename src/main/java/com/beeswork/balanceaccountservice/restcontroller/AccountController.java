package com.beeswork.balanceaccountservice.restcontroller;


import com.beeswork.balanceaccountservice.util.Convert;
import com.beeswork.balanceaccountservice.vm.AccountVM;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/account")
public class AccountController extends BaseController {

    private final ObjectMapper objectMapper;

    @Autowired
    public AccountController(ObjectMapper objectMapper, Convert convert) {
        super(convert);
        this.objectMapper = objectMapper;
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveAccount(@Valid @RequestBody AccountVM accountVM,
                                              BindingResult bindingResult) throws JsonProcessingException {

        if (bindingResult.hasErrors()) return super.fieldErrorsResponse(bindingResult);

        System.out.println(accountVM);
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

}
